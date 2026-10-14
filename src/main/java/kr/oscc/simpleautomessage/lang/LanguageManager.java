package kr.oscc.simpleautomessage.lang;

import kr.oscc.simpleautomessage.SimpleAutoMessage;
import kr.oscc.simpleautomessage.config.PluginConfig;
import kr.oscc.simpleautomessage.message.MessageParser;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Map;

public final class LanguageManager {
    private final SimpleAutoMessage plugin;
    private final PluginConfig config;
    private YamlConfiguration selected;
    private YamlConfiguration fallback;

    public LanguageManager(SimpleAutoMessage plugin, PluginConfig config) {
        this.plugin = plugin;
        this.config = config;
        reload();
    }

    public void reload() {
        fallback = load("en_US");
        String requested = config.language();
        selected = load(requested);
        if (selected == null) {
            plugin.getLogger().warning("Language '" + requested + "' was not found. Falling back to en_US.");
            selected = fallback;
        }
        if (fallback == null) {
            fallback = new YamlConfiguration();
        }
        if (selected == null) {
            selected = fallback;
        }
    }

    private YamlConfiguration load(String locale) {
        File file = new File(plugin.getDataFolder(), "lang/SimpleAutoMessage-" + locale + ".yml");
        if (!file.isFile()) {
            return null;
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public String raw(String key) {
        String value = selected.getString(key);
        if (value == null) {
            value = fallback.getString(key);
        }
        return value != null ? value : key;
    }

    public Component component(String key) {
        return MessageParser.parseLegacy(raw(key));
    }

    public Component component(String key, Map<String, String> replacements) {
        String value = raw(key);
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            value = value.replace("%" + entry.getKey() + "%", entry.getValue());
        }
        return MessageParser.parseLegacy(value);
    }
}
