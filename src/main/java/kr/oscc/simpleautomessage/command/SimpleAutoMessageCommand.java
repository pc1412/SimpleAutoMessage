package kr.oscc.simpleautomessage.command;

import kr.oscc.simpleautomessage.SimpleAutoMessage;
import kr.oscc.simpleautomessage.message.MessageList;
import kr.oscc.simpleautomessage.message.MessageParser;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class SimpleAutoMessageCommand implements CommandExecutor, TabCompleter {
    private final SimpleAutoMessage plugin;

    public SimpleAutoMessageCommand(SimpleAutoMessage plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelp(sender);
            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);
        if (sub.equals("version") || args[0].equals("버전")) {
            sendVersion(sender);
            return true;
        }

        if (!isAdmin(sender)) {
            sender.sendMessage(plugin.language().component("no-permission"));
            return true;
        }

        switch (sub) {
            case "reload" -> {
                try {
                    plugin.reloadPlugin();
                    sender.sendMessage(plugin.language().component("config-reloaded"));
                } catch (Exception ex) {
                    plugin.getLogger().severe("Reload failed: " + ex.getMessage());
                    sender.sendMessage(plugin.language().component("reload-error"));
                }
            }
            case "list" -> sendList(sender);
            case "send" -> sendImmediate(sender, args);
            case "enable" -> {
                plugin.pluginConfig().setRuntimeEnabled(true);
                sender.sendMessage(plugin.language().component("enabled"));
            }
            case "disable" -> {
                plugin.pluginConfig().setRuntimeEnabled(false);
                sender.sendMessage(plugin.language().component("disabled"));
            }
            default -> sender.sendMessage(plugin.language().component("unknown-command"));
        }
        return true;
    }

    private boolean isAdmin(CommandSender sender) {
        return !(sender instanceof org.bukkit.entity.Player player)
                || player.isOp()
                || sender.hasPermission("simpleautomessage.admin");
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(plugin.language().component("help-header"));
        sender.sendMessage(plugin.language().component("help-public"));
        if (isAdmin(sender)) {
            sender.sendMessage(plugin.language().component("help-admin-1"));
            sender.sendMessage(plugin.language().component("help-admin-2"));
        }
    }

    private void sendVersion(CommandSender sender) {
        sender.sendMessage(MessageParser.parseLegacy("&aSimpleAutoMessage &c" + plugin.getPluginMeta().getVersion() + " &afor Paper 26.3+"));
        sender.sendMessage(MessageParser.parseLegacy("&aby pc1412 - Running : &c" + Bukkit.getVersion()));
    }

    private void sendList(CommandSender sender) {
        sender.sendMessage(plugin.language().component("list-header"));
        for (MessageList list : plugin.messages().lists().values()) {
            Map<String, String> vars = new HashMap<>();
            vars.put("list", list.id());
            vars.put("enabled", Boolean.toString(list.enabled()));
            vars.put("interval", Long.toString(list.intervalSeconds()));
            vars.put("count", Integer.toString(list.messages().size()));
            sender.sendMessage(plugin.language().component("list-entry", vars));
        }
    }

    private void sendImmediate(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(MessageParser.parseLegacy("&cUsage: /sam send <list> [index]"));
            return;
        }
        MessageList list = plugin.messages().get(args[1]);
        if (list == null) {
            sender.sendMessage(plugin.language().component("list-not-found", Map.of("list", args[1])));
            return;
        }

        String raw;
        if (args.length >= 3) {
            int index;
            try {
                index = Integer.parseInt(args[2]);
            } catch (NumberFormatException ex) {
                index = -1;
            }
            raw = list.at(index);
            if (raw == null) {
                sender.sendMessage(plugin.language().component("message-index-invalid", Map.of("count", Integer.toString(list.messages().size()))));
                return;
            }
        } else {
            raw = list.next();
            if (raw == null) return;
        }

        plugin.messages().dispatch(list, raw);
        sender.sendMessage(plugin.language().component("send-success", Map.of("list", list.id())));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> choices = new ArrayList<>(List.of("help", "version", "버전"));
            if (isAdmin(sender)) {
                choices.addAll(List.of("reload", "list", "send", "enable", "disable"));
            }
            return filter(choices, args[0]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("send") && isAdmin(sender)) {
            return filter(plugin.messages().lists().values().stream().map(MessageList::id).toList(), args[1]);
        }
        return List.of();
    }

    private List<String> filter(List<String> values, String token) {
        String lower = token.toLowerCase(Locale.ROOT);
        return values.stream().filter(v -> v.toLowerCase(Locale.ROOT).startsWith(lower)).toList();
    }
}
