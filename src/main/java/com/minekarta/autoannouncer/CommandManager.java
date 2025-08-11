package com.minekarta.autoannouncer;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            sender.sendMessage(ChatColor.GOLD + "Usage: /" + label + " <reload|add|setinterval>");
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
                sender.sendMessage(ChatColor.RED + "Unknown subcommand. Usage: /" + label + " <reload|add|setinterval>");
                return true;
        }
    }

    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }
        plugin.reloadPlugin();
        return true;
    }

    private boolean handleAdd(CommandSender sender, String[] args) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }
        if (args.length < 3 || !args[1].equalsIgnoreCase("chat")) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + args[0] + " add chat <message...>");
            return true;
        }

        String message = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        List<Map<?, ?>> messages = plugin.getConfig().getMapList("messages");

        Map<String, String> newMessage = new HashMap<>();
        newMessage.put("type", "CHAT");
        newMessage.put("text", message);

        messages.add(newMessage);
        plugin.getConfig().set("messages", messages);
        plugin.saveConfig();

        sender.sendMessage(ChatColor.GREEN + "Chat message added successfully! Reload the plugin (/maa reload) to apply changes.");
        return true;
    }

    private boolean handleSetInterval(CommandSender sender, String[] args) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }
        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + args[0] + " setinterval <seconds>");
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
            sender.sendMessage(ChatColor.GREEN + "Interval set to " + interval + " seconds. Reload the plugin (/maa reload) to apply changes.");
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid number format. Please provide a whole number for the interval.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!sender.hasPermission(permission)) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            List<String> subcommands = Arrays.asList("reload", "add", "setinterval");
            return subcommands.stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("add")) {
            return Collections.singletonList("chat");
        }

        return Collections.emptyList();
    }
}
