package dev.velora.keyAllReloaded;

import dev.velora.keyAllReloaded.commands.ReloadCommand;
import dev.velora.keyAllReloaded.manager.CommandManager;
import dev.velora.keyAllReloaded.manager.TimerManager;
import dev.velora.keyAllReloaded.papi.KeyAllTimerExpansion;
import org.bukkit.plugin.java.JavaPlugin;

public final class KeyAllReloaded extends JavaPlugin {

    private CommandManager commandManager;

    @Override
    public void onEnable() {

        saveDefaultConfig();

        // Start the timer :D
        TimerManager timerManager = new TimerManager(this);
        timerManager.reloadConfigAndRestartTimer();

        // Check for PAPI
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new KeyAllTimerExpansion(timerManager).register();
            getLogger().info("PlaceholderAPI expansion registered successfully!");
        } else {
            getLogger().warning("PlaceholderAPI not found. Placeholder expansion not registered.");
        }

        int pluginId = 24279;
        Metrics metrics = new Metrics(this, pluginId);

        commandManager = new CommandManager(this);
        commandManager.registerCommands(new ReloadCommand(this, timerManager));

    }

    @Override
    public void onDisable() {
        // Do nun
    }
}
