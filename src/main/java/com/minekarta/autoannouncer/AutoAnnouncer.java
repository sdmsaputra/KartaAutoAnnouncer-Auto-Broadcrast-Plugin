package com.minekarta.autoannouncer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AutoAnnouncer extends JavaPlugin {

    private BukkitTask announcementTask;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        // Register command executor
        Objects.requireNonNull(getCommand("minekartaautoannouncer"), "Command not found in plugin.yml")
               .setExecutor(new CommandManager(this));
        startAnnouncerTask();
    }

    public void reloadPlugin() {
        // Cancel the current task
        if (announcementTask != null && !announcementTask.isCancelled()) {
            announcementTask.cancel();
        }
        // Reload the config file from disk
        reloadConfig();
        // Start the task with the new settings
        startAnnouncerTask();
        getLogger().info("AutoAnnouncer configuration reloaded.");
    }

    private void startAnnouncerTask() {
        // Load configuration
        String prefix = getConfig().getString("prefix", "&d[Minekarta] &r");
        int interval = getConfig().getInt("interval", 60);
        List<Map<?, ?>> messageMaps = getConfig().getMapList("messages");

        // Parse messages from the config into Announcement objects
        List<Announcement> announcements = new ArrayList<>();
        for (Map<?, ?> map : messageMaps) {
            String typeStr = (String) map.get("type");
            String text = (String) map.get("text");

            if (typeStr == null || text == null) {
                getLogger().warning("A message in config.yml is missing 'type' or 'text'. Skipping it.");
                continue;
            }

            try {
                AnnouncementType type = AnnouncementType.valueOf(typeStr.toUpperCase());
                String subtitle = (String) map.get("subtitle");
                announcements.add(new Announcement(type, text, subtitle));
            } catch (IllegalArgumentException e) {
                getLogger().warning("Invalid message type '" + typeStr + "' in config.yml. Skipping it.");
            }
        }

        if (announcements.isEmpty()) {
            getLogger().warning("No valid messages found in config.yml. The announcer will not start.");
            return;
        }

        if (interval <= 0) {
            getLogger().warning("The announcement interval must be positive. The announcer will not start.");
            return;
        }

        // Schedule the announcement task
        long period = interval * 20L;
        announcementTask = new AnnouncementTask(this, announcements, prefix).runTaskTimer(this, 0L, period);

        getLogger().info("AutoAnnouncer task has been started/reloaded with " + announcements.size() + " messages, running every " + interval + " seconds.");
    }

    @Override
    public void onDisable() {
        // Cancel the task when the plugin is disabled
        if (announcementTask != null && !announcementTask.isCancelled()) {
            announcementTask.cancel();
        }
        getLogger().info("Minekarta-Auto-Announcer has been disabled.");
    }

    // Helper method to convert strings with legacy/hex codes to Adventure Components
    public static Component createComponent(String text) {
        if (text == null) {
            return Component.empty();
        }
        // This serializer handles both legacy '&' codes and hex '&#RRGGBB' codes
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }
}
