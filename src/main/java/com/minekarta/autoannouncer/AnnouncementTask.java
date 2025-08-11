package com.minekarta.autoannouncer;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class AnnouncementTask extends BukkitRunnable {

    private final AutoAnnouncer plugin;
    private final List<Announcement> announcements;
    private final String prefix;
    private final boolean papiEnabled;
    private int messageIndex = 0;

    public AnnouncementTask(AutoAnnouncer plugin, List<Announcement> announcements, String prefix, boolean papiEnabled) {
        this.plugin = plugin;
        this.announcements = announcements;
        this.prefix = prefix;
        this.papiEnabled = papiEnabled;
    }

    @Override
    public void run() {
        if (announcements.isEmpty() || Bukkit.getOnlinePlayers().isEmpty()) {
            return;
        }

        Announcement announcement = announcements.get(messageIndex);

        for (Player player : Bukkit.getOnlinePlayers()) {
            String rawText = announcement.getText();
            String rawSubtitle = announcement.getSubtitle();

            // Parse placeholders only if PAPI is enabled and the text is not null
            String parsedText = (papiEnabled && rawText != null) ? PlaceholderAPI.setPlaceholders(player, rawText) : rawText;
            String parsedSubtitle = (papiEnabled && rawSubtitle != null) ? PlaceholderAPI.setPlaceholders(player, rawSubtitle) : rawSubtitle;

            Component textComponent = AutoAnnouncer.createComponent(parsedText);
            Component subtitleComponent = AutoAnnouncer.createComponent(parsedSubtitle);

            switch (announcement.getType()) {
                case CHAT:
                    Component prefixComponent = AutoAnnouncer.createComponent(prefix);
                    player.sendMessage(prefixComponent.append(textComponent));
                    break;
                case ACTION_BAR:
                    player.sendActionBar(textComponent);
                    break;
                case TITLE:
                    Title title = Title.title(textComponent, subtitleComponent);
                    player.showTitle(title);
                    break;
            }
        }

        // Move to the next message for the next execution
        messageIndex++;
        if (messageIndex >= announcements.size()) {
            messageIndex = 0; // Reset to the start
        }
    }
}
