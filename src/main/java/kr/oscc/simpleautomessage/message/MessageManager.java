package kr.oscc.simpleautomessage.message;

import kr.oscc.simpleautomessage.SimpleAutoMessage;
import kr.oscc.simpleautomessage.config.PluginConfig;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class MessageManager {
    private final SimpleAutoMessage plugin;
    private final PluginConfig config;
    private final Map<String, MessageList> lists = new LinkedHashMap<>();
    private final Map<String, BukkitTask> tasks = new LinkedHashMap<>();

    public MessageManager(SimpleAutoMessage plugin, PluginConfig config) {
        this.plugin = plugin;
        this.config = config;
        reload();
    }

    public void reload() {
        lists.clear();
        ConfigurationSection section = config.messageListsSection();
        if (section == null) return;

        for (String id : section.getKeys(false)) {
            ConfigurationSection listSection = section.getConfigurationSection(id);
            if (listSection == null) continue;
            boolean enabled = listSection.getBoolean("enabled", true);
            long interval = listSection.getLong("interval", 600L);
            int expiry = listSection.getInt("expiry", -1);
            boolean random = listSection.getBoolean("random", false);
            List<String> messages = listSection.getStringList("messages");
            lists.put(id.toLowerCase(), new MessageList(id, enabled, interval, expiry, random, messages));
        }
    }

    public void startAll() {
        stopAll();
        for (MessageList list : lists.values()) {
            if (!list.enabled() || list.messages().isEmpty() || list.expired()) continue;
            long ticks = Math.max(20L, list.intervalSeconds() * 20L);
            BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> runNext(list), ticks, ticks);
            tasks.put(list.id().toLowerCase(), task);
        }
    }

    public void stopAll() {
        tasks.values().forEach(BukkitTask::cancel);
        tasks.clear();
    }

    public void runNext(MessageList list) {
        if (!config.enabled() || list.expired()) return;
        String message = list.next();
        if (message == null) return;

        // The rotation advances even when the online-player condition is not met.
        if (Bukkit.getOnlinePlayers().size() < config.minPlayers()) return;
        dispatch(list, message);
    }

    public void dispatch(MessageList list, String raw) {
        String trimmed = raw.stripLeading();
        if (trimmed.startsWith("/")) {
            String command = trimmed.substring(1);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            if (config.logToConsole()) {
                plugin.getLogger().info("[" + list.id() + "] /" + command);
            }
            return;
        }

        try {
            Component component = list.id().equalsIgnoreCase("json")
                    ? MessageParser.parseJson(raw)
                    : MessageParser.parseLegacy(raw);
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(component);
            }
            if (config.logToConsole()) {
                plugin.getComponentLogger().info(component);
            }
        } catch (Exception ex) {
            plugin.getLogger().warning("Failed to parse message in list '" + list.id() + "': " + ex.getMessage());
        }
    }

    public Map<String, MessageList> lists() {
        return Map.copyOf(lists);
    }

    public MessageList get(String id) {
        if (id == null) return null;
        return lists.get(id.toLowerCase());
    }
}
