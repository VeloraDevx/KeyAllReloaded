package dev.velora.keyAllReloaded.commands;

import dev.velora.keyAllReloaded.KeyAllReloaded;
import dev.velora.keyAllReloaded.manager.CommandInfo;
import dev.velora.keyAllReloaded.manager.TimerManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ReloadCommand  {

    private final KeyAllReloaded plugin;
    private final TimerManager timerManager;

    public ReloadCommand(KeyAllReloaded plugin, TimerManager timerManager) {
        this.plugin = plugin;
        this.timerManager = timerManager;
    }

    @CommandInfo(
            name = "keyall",
            description = "Reload command for KeyAllReloaded",
            permission = "keyallreloaded.reload",
            aliases = "kar",
            tabComplete = {"reload"}

    )
    public void onReload(CommandSender sender, String[] args) {

        if (args.length > 1) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou don't have permissions to execute this command"));
            return;
        }

        // Reload the config
        timerManager.stopTimer();
        timerManager.reloadConfigAndRestartTimer();
        sender.sendMessage("Plugin has been reloaded!");

    }
}
