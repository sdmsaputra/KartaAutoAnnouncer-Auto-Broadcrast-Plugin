package com.minekarta.autoannouncer;

import org.bukkit.scheduler.BukkitRunnable;
import java.util.List;

public class AnnouncementTask extends BukkitRunnable {

    private final AutoAnnouncer plugin;
    private final String prefix;
    private final List<String> messages;
    private int messageIndex = 0;

    public AnnouncementTask(AutoAnnouncer plugin, String prefix, List<String> messages) {
        this.plugin = plugin;
        this.prefix = prefix;
        this.messages = messages;
    }

    @Override
    public void run() {
        // Ensure the message list is not empty to avoid errors
        if (messages.isEmpty()) {
            return;
        }

        // Get the current message
        String message = messages.get(messageIndex);

        // Combine prefix and message, then colorize the whole thing
        String finalMessage = prefix + message;

        // Broadcast the colorized message
        plugin.getServer().broadcastMessage(AutoAnnouncer.colorize(finalMessage));

        // Move to the next message for the next execution
        messageIndex++;
        if (messageIndex >= messages.size()) {
            messageIndex = 0; // Reset to the start
        }
    }
}
