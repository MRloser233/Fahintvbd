package com.fahintv.app;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Store {
    private static final String PREFS = "fahin_tv_store";

    public static Set<String> favorites(Context context) {
        return new LinkedHashSet<>(prefs(context).getStringSet("favorites", new LinkedHashSet<>()));
    }

    public static void toggleFavorite(Context context, Channel channel) {
        Set<String> values = favorites(context);
        if (values.contains(channel.url)) values.remove(channel.url);
        else values.add(channel.url);
        prefs(context).edit().putStringSet("favorites", values).apply();
    }

    public static boolean isFavorite(Context context, Channel channel) {
        return favorites(context).contains(channel.url);
    }

    public static void markWatched(Context context, Channel channel) {
        ArrayList<String> values = new ArrayList<>(prefs(context).getStringSet("history", new LinkedHashSet<>()));
        values.remove(channel.url);
        values.add(0, channel.url);
        while (values.size() > 24) values.remove(values.size() - 1);
        prefs(context).edit().putStringSet("history", new LinkedHashSet<>(values)).apply();
    }

    public static List<Channel> channelsForUrls(Context context, List<Channel> source, String key) {
        Set<String> urls = prefs(context).getStringSet(key, new LinkedHashSet<>());
        ArrayList<Channel> result = new ArrayList<>();
        for (String url : urls) {
            for (Channel channel : source) {
                if (channel.url.equals(url)) {
                    result.add(channel);
                    break;
                }
            }
        }
        return result;
    }

    public static long position(Context context, Channel channel) {
        return prefs(context).getLong("pos_" + channel.url.hashCode(), 0L);
    }

    public static void savePosition(Context context, Channel channel, long positionMs) {
        prefs(context).edit().putLong("pos_" + channel.url.hashCode(), positionMs).apply();
    }

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }
}
