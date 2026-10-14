package kr.oscc.simpleautomessage.config;

import kr.oscc.simpleautomessage.SimpleAutoMessage;
import org.bukkit.configuration.ConfigurationSection;

public final class PluginConfig {
    private final SimpleAutoMessage plugin;
    private boolean runtimeEnabled;

    public PluginConfig(SimpleAutoMessage plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        runtimeEnabled = plugin.getConfig().getBoolean("settings.enabled", true);
    }

    public String language() {
        return plugin.getConfig().getString("settings.language", "ko_KR");
    }

    public boolean enabled() {
        return runtimeEnabled;
    }

    public void setRuntimeEnabled(boolean enabled) {
        runtimeEnabled = enabled;
    }

    public int minPlayers() {
        return Math.max(0, plugin.getConfig().getInt("settings.min-players", 0));
    }

    public boolean logToConsole() {
        return plugin.getConfig().getBoolean("settings.log-to-console", false);
    }

    public ConfigurationSection messageListsSection() {
        return plugin.getConfig().getConfigurationSection("message-lists");
    }
}
