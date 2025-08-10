package com.minekarta.autoannouncer;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.ChatColor;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoAnnouncer extends JavaPlugin {

    private BukkitTask announcementTask;
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    @Override
    public void onEnable() {
        // Save the default config.yml from the JAR if it doesn't exist
        saveDefaultConfig();

        // Load configuration
        String prefix = getConfig().getString("prefix", "&d[Minekarta] &r");
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
        announcementTask = new AnnouncementTask(this, prefix, messages).runTaskTimer(this, 0L, period);

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

    // Helper method to translate color codes, now with hex support
    public static String colorize(String message) {
        if (message == null) {
            return "";
        }

        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            // Using Bukkit's ChatColor.of for modern versions (1.16+)
            // This requires the server to be on a version that supports it.
            // Spigot API bundles the necessary classes from BungeeCord.
            matcher.appendReplacement(buffer, net.md_5.bungee.api.ChatColor.of("#" + matcher.group(1)).toString());
        }
        matcher.appendTail(buffer);

        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }
}
