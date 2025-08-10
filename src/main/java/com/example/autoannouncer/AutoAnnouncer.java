package com.example.autoannouncer;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.ChatColor;
import java.util.List;

public class AutoAnnouncer extends JavaPlugin {

    private BukkitTask announcementTask;

    @Override
    public void onEnable() {
        // Save the default config.yml from the JAR if it doesn't exist
        saveDefaultConfig();

        // Load configuration
        int interval = getConfig().getInt("interval", 60);
        List<String> messages = getConfig().getStringList("messages");

        // Basic validation
        if (messages.isEmpty()) {
            getLogger().warning("No messages found in config.yml. The announcer will not start.");
            return;
        }

        if (interval <= 0) {
            getLogger().warning("The announcement interval must be positive. The announcer will not start.");
            return;
        }

        // Schedule the announcement task
        // The period is in ticks (20 ticks = 1 second)
        long period = interval * 20L;
        announcementTask = new AnnouncementTask(this, messages).runTaskTimer(this, 0L, period);

        getLogger().info("AutoAnnouncer has been enabled with " + messages.size() + " messages, running every " + interval + " seconds.");
    }

    @Override
    public void onDisable() {
        // Cancel the task when the plugin is disabled
        if (announcementTask != null && !announcementTask.isCancelled()) {
            announcementTask.cancel();
        }
        getLogger().info("AutoAnnouncer has been disabled.");
    }

    // Helper method to translate color codes
    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
