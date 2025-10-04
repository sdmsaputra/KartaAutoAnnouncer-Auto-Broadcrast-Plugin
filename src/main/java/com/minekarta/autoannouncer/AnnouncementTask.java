package com.minekarta.autoannouncer;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class AnnouncementTask extends BukkitRunnable {

    private final List<Announcement> announcements;
    private final String prefix;
    private final boolean papiEnabled;
    private final boolean prefixEnabled;
    private int messageIndex = 0;

    public AnnouncementTask(List<Announcement> announcements, String prefix, boolean papiEnabled, boolean prefixEnabled) {
        this.announcements = announcements;
        this.prefix = prefix;
        this.papiEnabled = papiEnabled;
        this.prefixEnabled = prefixEnabled;
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

            String parsedText = (papiEnabled && rawText != null) ? PlaceholderAPI.setPlaceholders(player, rawText) : rawText;
            String parsedSubtitle = (papiEnabled && rawSubtitle != null) ? PlaceholderAPI.setPlaceholders(player, rawSubtitle) : rawSubtitle;

            Component textComponent = AutoAnnouncer.createComponent(parsedText);
            Component subtitleComponent = AutoAnnouncer.createComponent(parsedSubtitle);

            switch (announcement.getType()) {
                case CHAT:
                    if (parsedText != null && parsedText.contains("\n")) {
                        String[] lines = parsedText.split("\n");
                        for (String line : lines) {
                            Component lineComponent = AutoAnnouncer.createComponent(line);
                            if (prefixEnabled) {
                                Component prefixComponent = AutoAnnouncer.createComponent(prefix);
                                player.sendMessage(prefixComponent.append(lineComponent));
                            } else {
                                player.sendMessage(lineComponent);
                            }
                        }
                    } else {
                        if (prefixEnabled) {
                            Component prefixComponent = AutoAnnouncer.createComponent(prefix);
                            player.sendMessage(prefixComponent.append(textComponent));
                        } else {
                            player.sendMessage(textComponent);
                        }
                    }
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

        messageIndex = (messageIndex + 1) % announcements.size();
    }
}
