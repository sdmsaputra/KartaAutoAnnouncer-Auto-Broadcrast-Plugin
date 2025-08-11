package com.minekarta.autoannouncer;

public class Announcement {

    private final AnnouncementType type;
    private final String text;
    private final String subtitle;

    public Announcement(AnnouncementType type, String text, String subtitle) {
        this.type = type;
        this.text = text;
        this.subtitle = subtitle;
    }

    public AnnouncementType getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public String getSubtitle() {
        return subtitle;
    }
}
