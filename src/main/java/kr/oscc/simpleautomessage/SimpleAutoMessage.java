package kr.oscc.simpleautomessage;

import kr.oscc.simpleautomessage.command.SimpleAutoMessageCommand;
import kr.oscc.simpleautomessage.config.PluginConfig;
import kr.oscc.simpleautomessage.lang.LanguageManager;
import kr.oscc.simpleautomessage.message.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class SimpleAutoMessage extends JavaPlugin {
    private PluginConfig pluginConfig;
    private LanguageManager languageManager;
    private MessageManager messageManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveBundledLanguage("SimpleAutoMessage-ko_KR.yml");
        saveBundledLanguage("SimpleAutoMessage-en_US.yml");

        pluginConfig = new PluginConfig(this);
        languageManager = new LanguageManager(this, pluginConfig);
        messageManager = new MessageManager(this, pluginConfig);

        SimpleAutoMessageCommand commandHandler = new SimpleAutoMessageCommand(this);
        PluginCommand command = getCommand("simpleautomessage");
        if (command != null) {
            command.setExecutor(commandHandler);
            command.setTabCompleter(commandHandler);
        }

        messageManager.startAll();
        printVersionBanner();
    }

    @Override
    public void onDisable() {
        if (messageManager != null) {
            messageManager.stopAll();
        }
    }

    public void reloadPlugin() {
        if (messageManager != null) {
            messageManager.stopAll();
        }
        reloadConfig();
        pluginConfig.reload();
        languageManager.reload();
        messageManager.reload();
        messageManager.startAll();
    }

    private void saveBundledLanguage(String fileName) {
        java.io.File target = new java.io.File(getDataFolder(), "lang/" + fileName);
        if (!target.exists()) {
            saveResource("lang/" + fileName, false);
        }
    }

    public void printVersionBanner() {
        getComponentLogger().info(kr.oscc.simpleautomessage.message.MessageParser.parseLegacy(
                "&aSimpleAutoMessage &c" + getPluginMeta().getVersion() + " &afor Paper 26.3+"));
        getComponentLogger().info(kr.oscc.simpleautomessage.message.MessageParser.parseLegacy(
                "&aby pc1412 - Running : &c" + Bukkit.getVersion()));
    }

    public PluginConfig pluginConfig() {
        return pluginConfig;
    }

    public LanguageManager language() {
        return languageManager;
    }

    public MessageManager messages() {
        return messageManager;
    }
}
