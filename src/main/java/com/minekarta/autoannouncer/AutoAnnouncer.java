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
    public boolean papiEnabled = false;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            this.papiEnabled = true;
            getLogger().info("Successfully hooked into PlaceholderAPI.");
        }

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
        String prefix = getConfig().getString("prefix", "&d[Minekarta] &r");
        int interval = getConfig().getInt("interval", 60);

        List<Announcement> announcements = new ArrayList<>();
        if (getConfig().isConfigurationSection("announcements")) {
            // Chat messages
            getConfig().getStringList("announcements.chat").forEach(text ->
                    announcements.add(new Announcement(AnnouncementType.CHAT, text, null))
            );

            // Action bar messages
            getConfig().getStringList("announcements.action_bar").forEach(text ->
                    announcements.add(new Announcement(AnnouncementType.ACTION_BAR, text, null))
            );

            // Title messages
            if (getConfig().isList("announcements.title")) {
                getConfig().getMapList("announcements.title").forEach(map -> {
                    String title = (String) map.get("title");
                    String subtitle = (String) map.get("subtitle");
                    if (title != null) {
                        announcements.add(new Announcement(AnnouncementType.TITLE, title, subtitle));
                    }
                });
            }
        }

        if (announcements.isEmpty()) {
            getLogger().warning("No valid announcements found in config.yml. The announcer will not start.");
            return;
        }

        if (interval <= 0) {
            getLogger().warning("The announcement interval must be positive. The announcer will not start.");
            return;
        }

        long period = interval * 20L;
        announcementTask = new AnnouncementTask(this, announcements, prefix, papiEnabled).runTaskTimer(this, 0L, period);

        getLogger().info("AutoAnnouncer task has been started/reloaded with " + announcements.size() + " announcements, running every " + interval + " seconds.");
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
