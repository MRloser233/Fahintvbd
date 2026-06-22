package com.fahintv.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity {
    private final List<String> categories = Arrays.asList("Live Now", "World Cup", "Football", "Cricket", "Bangladesh Channels", "USA", "International Sports", "Favorites", "Recently Watched", "Continue Watching", "Settings");
    private final ArrayList<Channel> channels = new ArrayList<>();
    private LinearLayout sections;
    private String active = "Live Now";
    private String query = "";

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        channels.addAll(M3uParser.fromAssets(this));
        setContentView(buildHome());
        render();
    }

    private View buildHome() {
        LinearLayout root = column();
        root.setBackgroundColor(Color.rgb(8, 11, 16));
        root.setPadding(dp(18), dp(18), dp(18), dp(8));

        TextView title = label("Fahin TV", 32, true);
        title.setTextColor(Color.rgb(0, 208, 132));
        root.addView(title);
        root.addView(label("Fast sports streaming for World Cup, Football, Cricket and live channels", 13, false));

        EditText search = new EditText(this);
        search.setHint("Search channels");
        search.setSingleLine(true);
        search.setTextColor(Color.WHITE);
        search.setHintTextColor(Color.rgb(140, 151, 165));
        search.setTextSize(15);
        search.setPadding(dp(14), 0, dp(14), 0);
        search.setBackgroundColor(Color.rgb(28, 38, 52));
        search.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                query = s.toString();
                render();
            }
            public void afterTextChanged(Editable s) {}
        });
        root.addView(search, params(-1, dp(48), 12, 10));

        HorizontalScrollView chipScroll = new HorizontalScrollView(this);
        chipScroll.setHorizontalScrollBarEnabled(false);
        LinearLayout chips = row();
        for (String category : categories) chips.addView(chip(category));
        chipScroll.addView(chips);
        root.addView(chipScroll, params(-1, dp(48), 0, 8));

        ScrollView scroll = new ScrollView(this);
        sections = column();
        scroll.addView(sections);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        return root;
    }

    private TextView chip(String text) {
        TextView view = label(text, 14, true);
        view.setGravity(Gravity.CENTER);
        view.setPadding(dp(14), 0, dp(14), 0);
        view.setBackgroundColor(text.equals(active) ? Color.rgb(0, 208, 132) : Color.rgb(28, 38, 52));
        view.setTextColor(text.equals(active) ? Color.rgb(8, 11, 16) : Color.WHITE);
        view.setOnClickListener(v -> {
            active = text;
            setContentView(buildHome());
            render();
        });
        chipsFocus(view);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, dp(40));
        lp.setMargins(0, 0, dp(8), 0);
        view.setLayoutParams(lp);
        return view;
    }

    private void render() {
        sections.removeAllViews();
        if ("Settings".equals(active)) {
            sections.addView(sectionTitle("Settings"));
            sections.addView(info("Playlist: fahin_tv_channels.m3u\nFeatures: M3U parsing, favorites, history, resume position, PiP, low-latency buffer profile, auto reconnect, TV launcher support.\nCloud login and EPG endpoints are ready to connect in the next backend phase."));
            return;
        }
        if ("Favorites".equals(active)) {
            addSection("Favorites", Store.channelsForUrls(this, channels, "favorites"));
            return;
        }
        if ("Recently Watched".equals(active) || "Continue Watching".equals(active)) {
            addSection(active, Store.channelsForUrls(this, channels, "history"));
            return;
        }
        addSection(active, filtered(active));
        if ("Live Now".equals(active)) {
            addSection("World Cup", filtered("World Cup"));
            addSection("Football", filtered("Football"));
            addSection("Cricket", filtered("Cricket"));
            addSection("Bangladesh Channels", filtered("Bangladesh Channels"));
            addSection("International Sports", filtered("International Sports"));
        }
    }

    private List<Channel> filtered(String category) {
        ArrayList<Channel> result = new ArrayList<>();
        for (Channel channel : channels) {
            boolean inCategory = "Live Now".equals(category)
                    || category.equals(channel.category)
                    || ("USA".equals(category) && "USA".equals(channel.country));
            if (inCategory && channel.matches(query)) result.add(channel);
            if (result.size() >= 36) break;
        }
        return result;
    }

    private void addSection(String title, List<Channel> list) {
        sections.addView(sectionTitle(title));
        if (list.isEmpty()) {
            sections.addView(info("No channels found"));
            return;
        }
        LinearLayout grid = column();
        for (Channel channel : list) grid.addView(card(channel));
        sections.addView(grid);
    }

    private TextView sectionTitle(String text) {
        TextView view = label(text, 22, true);
        view.setPadding(0, dp(16), 0, dp(8));
        return view;
    }

    private View card(Channel channel) {
        LinearLayout card = row();
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(14), dp(12), dp(10), dp(12));
        card.setBackgroundColor(Color.rgb(17, 23, 34));
        chipsFocus(card);

        TextView play = label("▶", 24, true);
        play.setTextColor(Color.rgb(255, 176, 0));
        play.setGravity(Gravity.CENTER);
        card.addView(play, new LinearLayout.LayoutParams(dp(42), dp(42)));

        LinearLayout copy = column();
        TextView name = label(channel.name, 16, true);
        TextView meta = label(channel.category + " • " + channel.country, 12, false);
        meta.setTextColor(Color.rgb(146, 158, 176));
        copy.addView(name);
        copy.addView(meta);
        card.addView(copy, new LinearLayout.LayoutParams(0, -2, 1));

        TextView fav = label(Store.isFavorite(this, channel) ? "★" : "☆", 24, true);
        fav.setTextColor(Color.rgb(0, 208, 132));
        fav.setGravity(Gravity.CENTER);
        fav.setOnClickListener(v -> {
            Store.toggleFavorite(this, channel);
            render();
        });
        card.addView(fav, new LinearLayout.LayoutParams(dp(48), dp(48)));

        card.setOnClickListener(v -> {
            Store.markWatched(this, channel);
            Intent intent = new Intent(this, PlayerActivity.class);
            intent.putExtra("channel", channel);
            startActivity(intent);
        });
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, 0, 0, dp(8));
        card.setLayoutParams(lp);
        return card;
    }

    private TextView info(String text) {
        TextView view = label(text, 14, false);
        view.setPadding(dp(14), dp(14), dp(14), dp(14));
        view.setBackgroundColor(Color.rgb(17, 23, 34));
        return view;
    }

    private LinearLayout column() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        return layout;
    }

    private LinearLayout row() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        return layout;
    }

    private TextView label(String text, int sp, boolean bold) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextColor(Color.WHITE);
        view.setTextSize(sp);
        if (bold) view.setTypeface(Typeface.DEFAULT_BOLD);
        return view;
    }

    private LinearLayout.LayoutParams params(int w, int h, int top, int bottom) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(w, h);
        lp.setMargins(0, dp(top), 0, dp(bottom));
        return lp;
    }

    private void chipsFocus(View view) {
        view.setFocusable(true);
        view.setOnFocusChangeListener((v, hasFocus) -> v.setAlpha(hasFocus ? 0.78f : 1f));
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }
}
