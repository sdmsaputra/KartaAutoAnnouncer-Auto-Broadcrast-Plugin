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
    private AnnouncementMessages announcementMessages;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        announcementMessages = new AnnouncementMessages(this);

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            papiEnabled = true;
            getLogger().info("Successfully hooked into PlaceholderAPI.");
        }

        Objects.requireNonNull(getCommand("kartaautoannouncer"), "Command not found in plugin.yml")
                .setExecutor(new CommandManager(this));

        startAnnouncerTask();
    }

    public void reloadPlugin() {
        if (announcementTask != null && !announcementTask.isCancelled()) {
            announcementTask.cancel();
        }
        reloadConfig();
        announcementMessages.loadAnnouncements();
        startAnnouncerTask();
        getLogger().info("KartaAutoAnnouncer configuration reloaded.");
    }

    private void startAnnouncerTask() {
        announcementMessages.loadAnnouncements();
        List<Announcement> announcements = announcementMessages.getAnnouncements();

        if (announcements.isEmpty()) {
            getLogger().warning("No announcements loaded. The announcer will not start.");
            return;
        }

        int interval = getConfig().getInt("interval", 60);
        if (interval <= 0) {
            getLogger().warning("The announcement interval must be positive. The announcer will not start.");
            return;
        }

        String prefix = getConfig().getString("prefix.text", "&d[Karta] &r");
        boolean prefixEnabled = getConfig().getBoolean("prefix.enabled", true);

        long period = interval * 20L;
        announcementTask = new AnnouncementTask(announcements, prefix, papiEnabled, prefixEnabled)
                .runTaskTimer(this, 0L, period);

        getLogger().info("AutoAnnouncer task started with " + announcements.size() + " announcements, running every " + interval + " seconds.");
    }

    @Override
    public void onDisable() {
        if (announcementTask != null && !announcementTask.isCancelled()) {
            announcementTask.cancel();
        }
        getLogger().info("KartaAutoAnnouncer has been disabled.");
    }

    public static Component createComponent(String text) {
        if (text == null) {
            return Component.empty();
        }
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }
}
