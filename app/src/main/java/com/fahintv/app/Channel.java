package com.fahintv.app;

import java.io.Serializable;
import java.util.Locale;

public class Channel implements Serializable {
    public final String name;
    public final String url;
    public final String logo;
    public final String category;
    public final String country;

    public Channel(String name, String url, String logo) {
        this.name = clean(name);
        this.url = url == null ? "" : url.trim();
        this.logo = logo == null ? "" : logo.trim();
        String lower = this.name.toLowerCase(Locale.US);
        this.category = detectCategory(lower);
        this.country = detectCountry(this.name, lower);
    }

    public boolean matches(String text) {
        String q = text == null ? "" : text.toLowerCase(Locale.US).trim();
        return q.isEmpty()
                || name.toLowerCase(Locale.US).contains(q)
                || category.toLowerCase(Locale.US).contains(q)
                || country.toLowerCase(Locale.US).contains(q);
    }

    private static String clean(String value) {
        if (value == null || value.trim().isEmpty()) return "Untitled Channel";
        return value.replace("ðŸ‡§ðŸ‡©", "BD")
                .replace("ðŸ‡ºðŸ‡¸", "USA")
                .replace("ðŸ‡®ðŸ‡³", "India")
                .replace("ðŸ‡µðŸ‡°", "Pakistan")
                .trim();
    }

    private static String detectCategory(String lower) {
        if (lower.contains("world cup") || lower.contains("fifa")) return "World Cup";
        if (lower.contains("football") || lower.contains("bein") || lower.contains("fox sports")) return "Football";
        if (lower.contains("cricket") || lower.contains("ptv sports") || lower.contains("willow")) return "Cricket";
        if (lower.contains("sports") || lower.contains("tsn") || lower.contains("espn")) return "International Sports";
        if (lower.contains("btv") || lower.contains("bangla") || lower.contains("asian tv")) return "Bangladesh Channels";
        return "Live Now";
    }

    private static String detectCountry(String original, String lower) {
        if (original.contains("🇧🇩") || lower.contains("bangladesh") || lower.contains("bangla") || lower.contains("btv")) return "Bangladesh";
        if (original.contains("🇺🇸") || lower.contains("usa") || lower.contains("fox") || lower.contains("espn")) return "USA";
        if (original.contains("🇮🇳") || lower.contains("india") || lower.contains("hindi")) return "India";
        if (original.contains("🇵🇰") || lower.contains("pakistan") || lower.contains("ptv")) return "Pakistan";
        if (lower.contains("canada") || lower.contains("tsn")) return "Canada";
        if (lower.contains("brazil") || lower.contains("caze")) return "Brazil";
        return "International";
    }
}
