package com.fahintv.app;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class M3uParser {
    private static final Pattern LOGO = Pattern.compile("tvg-logo=\"([^\"]*)\"");

    public static List<Channel> fromAssets(Context context) {
        ArrayList<Channel> channels = new ArrayList<>();
        try (InputStream stream = context.getAssets().open("fahin_tv_channels.m3u");
             BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String name = "";
            String logo = "";
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("#EXTINF")) {
                    int comma = line.lastIndexOf(',');
                    name = comma >= 0 ? line.substring(comma + 1) : "Live Channel";
                    Matcher matcher = LOGO.matcher(line);
                    logo = matcher.find() ? matcher.group(1) : "";
                } else if (!line.isEmpty() && !line.startsWith("#")) {
                    channels.add(new Channel(name, line, logo));
                }
            }
        } catch (Exception ignored) {
        }
        return channels;
    }
}
