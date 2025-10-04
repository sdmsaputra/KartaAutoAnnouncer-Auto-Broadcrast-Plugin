package com.minekarta.autoannouncer;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnnouncementMessages {
    private final AutoAnnouncer plugin;
    private final List<Announcement> announcements = new ArrayList<>();

    public AnnouncementMessages(AutoAnnouncer plugin) {
        this.plugin = plugin;
    }

    public void loadAnnouncements() {
        announcements.clear();
        FileConfiguration config = plugin.getConfig();

        if (!config.isConfigurationSection("announcements")) {
            return;
        }

        // Chat messages
        if (config.getBoolean("announcements.chat.enabled", false) && config.isList("announcements.chat.messages")) {
            config.getList("announcements.chat.messages").forEach(obj -> {
                if (obj instanceof String) {
                    announcements.add(new Announcement(AnnouncementType.CHAT, (String) obj, null));
                } else if (obj instanceof List) {
                    String message = String.join("\n", (List<String>) obj);
                    announcements.add(new Announcement(AnnouncementType.CHAT, message, null));
                }
            });
        }

        // Action bar messages
        if (config.getBoolean("announcements.action_bar.enabled", false) && config.isList("announcements.action_bar.messages")) {
            config.getStringList("announcements.action_bar.messages").forEach(text ->
                    announcements.add(new Announcement(AnnouncementType.ACTION_BAR, text, null))
            );
        }

        // Title messages
        if (config.getBoolean("announcements.title.enabled", false) && config.isList("announcements.title.messages")) {
            config.getMapList("announcements.title.messages").forEach(map -> {
                String title = (String) map.get("title");
                String subtitle = (String) map.get("subtitle");
                if (title != null) {
                    announcements.add(new Announcement(AnnouncementType.TITLE, title, subtitle));
                }
            });
        }
    }

    public List<Announcement> getAnnouncements() {
        return announcements;
    }
}