package com.fahintv.app;

import android.app.Activity;
import android.app.PictureInPictureParams;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Rational;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.DefaultLoadControl;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

public class PlayerActivity extends Activity {
    private Channel channel;
    private ExoPlayer player;
    private PlayerView playerView;
    private LinearLayout controls;
    private boolean locked;
    private final Handler reconnectHandler = new Handler();

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        channel = (Channel) getIntent().getSerializableExtra("channel");
        buildPlayerUi();
        startPlayer();
    }

    private void buildPlayerUi() {
        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.BLACK);
        playerView = new PlayerView(this);
        playerView.setUseController(true);
        root.addView(playerView, new FrameLayout.LayoutParams(-1, -1));

        controls = new LinearLayout(this);
        controls.setOrientation(LinearLayout.HORIZONTAL);
        controls.setGravity(Gravity.CENTER);
        controls.setPadding(dp(8), dp(8), dp(8), dp(8));
        controls.setBackgroundColor(Color.argb(170, 8, 11, 16));
        addButton("−10", v -> seekBy(-10_000));
        addButton("+10", v -> seekBy(10_000));
        addButton("−30", v -> seekBy(-30_000));
        addButton("+30", v -> seekBy(30_000));
        addButton("1x", v -> cycleSpeed((TextView) v));
        addButton("PiP", v -> enterPip());
        addButton("Lock", v -> {
            locked = !locked;
            playerView.setUseController(!locked);
            ((TextView) v).setText(locked ? "Unlock" : "Lock");
        });
        FrameLayout.LayoutParams cp = new FrameLayout.LayoutParams(-1, dp(58), Gravity.BOTTOM);
        root.addView(controls, cp);

        TextView title = new TextView(this);
        title.setText(channel == null ? "Fahin TV" : channel.name);
        title.setTextColor(Color.WHITE);
        title.setTextSize(18);
        title.setGravity(Gravity.CENTER_VERTICAL);
        title.setPadding(dp(14), 0, dp(14), 0);
        title.setBackgroundColor(Color.argb(130, 8, 11, 16));
        root.addView(title, new FrameLayout.LayoutParams(-1, dp(54), Gravity.TOP));
        setContentView(root);
    }

    private void startPlayer() {
        DefaultLoadControl loadControl = new DefaultLoadControl.Builder()
                .setBufferDurationsMs(8_000, 30_000, 900, 1_500)
                .setPrioritizeTimeOverSizeThresholds(true)
                .build();
        player = new ExoPlayer.Builder(this).setLoadControl(loadControl).build();
        playerView.setPlayer(player);
        if (channel != null) {
            player.setMediaItem(MediaItem.fromUri(Uri.parse(channel.url)));
            player.prepare();
            player.seekTo(Store.position(this, channel));
            player.play();
        }
        player.addListener(new Player.Listener() {
            @Override
            public void onPlayerError(PlaybackException error) {
                reconnectHandler.postDelayed(() -> {
                    if (player != null) {
                        player.prepare();
                        player.play();
                    }
                }, 1800);
            }
        });
    }

    private void addButton(String label, View.OnClickListener listener) {
        TextView button = new TextView(this);
        button.setText(label);
        button.setTextColor(Color.WHITE);
        button.setTextSize(13);
        button.setGravity(Gravity.CENTER);
        button.setBackgroundColor(Color.rgb(28, 38, 52));
        button.setOnClickListener(listener);
        button.setFocusable(true);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, dp(42), 1);
        lp.setMargins(dp(3), 0, dp(3), 0);
        controls.addView(button, lp);
    }

    private void seekBy(long delta) {
        if (player == null) return;
        long target = Math.max(0, player.getCurrentPosition() + delta);
        player.seekTo(target);
    }

    private void cycleSpeed(TextView view) {
        if (player == null) return;
        float next = player.getPlaybackParameters().speed == 1f ? 1.25f : player.getPlaybackParameters().speed == 1.25f ? 1.5f : 1f;
        player.setPlaybackSpeed(next);
        view.setText(next == 1f ? "1x" : next + "x");
    }

    private void enterPip() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PictureInPictureParams params = new PictureInPictureParams.Builder()
                    .setAspectRatio(new Rational(16, 9))
                    .build();
            enterPictureInPictureMode(params);
        }
    }

    @Override
    public void onUserLeaveHint() {
        super.onUserLeaveHint();
        enterPip();
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        controls.setVisibility(isInPictureInPictureMode ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null && channel != null) Store.savePosition(this, channel, player.getCurrentPosition());
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N && player != null) player.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        reconnectHandler.removeCallbacksAndMessages(null);
        if (player != null) {
            player.release();
            player = null;
        }
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }
}
