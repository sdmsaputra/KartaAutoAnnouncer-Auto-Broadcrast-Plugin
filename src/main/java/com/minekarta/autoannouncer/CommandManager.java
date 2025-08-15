package com.minekarta.autoannouncer;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class CommandManager implements CommandExecutor, TabCompleter {

    private final AutoAnnouncer plugin;
    private final String permission = "autoannouncer.admin";

    public CommandManager(AutoAnnouncer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sendUsage(sender, label);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "reload":
                return handleReload(sender);
            case "add":
                return handleAdd(sender, args);
            case "setinterval":
                return handleSetInterval(sender, args);
            default:
                sendUsage(sender, label);
                return true;
        }
    }

    private void sendUsage(CommandSender sender, String label) {
        sender.sendMessage(ChatColor.GOLD + "Usage: /" + label + " <reload|add|setinterval>");
    }

    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }
        plugin.reloadPlugin();
        sender.sendMessage(ChatColor.GREEN + "Configuration reloaded successfully!");
        return true;
    }

    private boolean handleAdd(CommandSender sender, String[] args) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /kaa add <type> <message...>");
            sender.sendMessage(ChatColor.RED + "Types: chat, actionbar, title");
            return true;
        }

        String type = args[1].toLowerCase();
        String message = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

        switch (type) {
            case "chat":
            case "actionbar":
                return addSimpleMessage(sender, type, message);
            case "title":
                return addTitleMessage(sender, message);
            default:
                sender.sendMessage(ChatColor.RED + "Invalid announcement type. Use 'chat', 'actionbar', or 'title'.");
                return true;
        }
    }

    private boolean addSimpleMessage(CommandSender sender, String type, String message) {
        String path = "announcements." + (type.equals("chat") ? "chat" : "action_bar");
        List<String> messages = plugin.getConfig().getStringList(path);
        messages.add(message);
        plugin.getConfig().set(path, messages);
        plugin.saveConfig();

        sender.sendMessage(ChatColor.GREEN + "Successfully added " + type + " announcement! Reload with /kaa reload to see changes.");
        return true;
    }

    private boolean addTitleMessage(CommandSender sender, String fullMessage) {
        String[] parts = fullMessage.split("\\|", 2);
        String title = parts[0].trim();
        String subtitle = (parts.length > 1) ? parts[1].trim() : "";

        List<Map<?, ?>> titles = plugin.getConfig().getMapList("announcements.title");
        Map<String, String> newTitle = new HashMap<>();
        newTitle.put("title", title);
        newTitle.put("subtitle", subtitle);
        titles.add(newTitle);

        plugin.getConfig().set("announcements.title", titles);
        plugin.saveConfig();

        sender.sendMessage(ChatColor.GREEN + "Successfully added title announcement! Reload with /kaa reload to see changes.");
        return true;
    }


    private boolean handleSetInterval(CommandSender sender, String[] args) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }
        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /kaa setinterval <seconds>");
            return true;
        }

        try {
            int interval = Integer.parseInt(args[1]);
            if (interval <= 0) {
                sender.sendMessage(ChatColor.RED + "Interval must be a positive number.");
                return true;
            }
            plugin.getConfig().set("interval", interval);
            plugin.saveConfig();
            sender.sendMessage(ChatColor.GREEN + "Interval set to " + interval + " seconds. Reload with /kaa reload to apply changes.");
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid number format.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!sender.hasPermission(permission)) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            return Arrays.asList("reload", "add", "setinterval").stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("add")) {
            return Arrays.asList("chat", "actionbar", "title").stream()
                    .filter(s -> s.startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
