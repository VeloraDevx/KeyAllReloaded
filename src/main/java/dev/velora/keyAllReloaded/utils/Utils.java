package dev.velora.keyAllReloaded.utils;

import dev.velora.keyAllReloaded.KeyAllReloaded;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Objects;

public class Utils {

    private final KeyAllReloaded plugin;
    private FileConfiguration config;

    public Utils(KeyAllReloaded plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    public void executeExternalFeatures(Audience audience, Player player) {
        sendMessage(audience);
        sendHotBarMessage(audience);
        sendTitle(audience);
        playSound(player);
    }

    public void sendMessage(Audience audience) {
        audience.sendMessage(Component.text(Objects.requireNonNull(translateLegacyColorCodes(config.getString("features.message")))));
    }

    public void sendHotBarMessage(Audience audience) {
        if (config.getBoolean("features.actionbar.enabled", true)) {
            audience.sendActionBar(Component.text(Objects.requireNonNull(translateLegacyColorCodes(config.getString("features.actionbar.message")))));
        }
    }

    public void sendTitle(Audience player) {

        if (config.getBoolean("features.title.enabled", true)) {
            String title = translateLegacyColorCodes(config.getString("features.title.title"));
            String subtitle = translateLegacyColorCodes(plugin.getConfig().getString("features.title.subtitle"));
            Duration fadeIn = Duration.ofSeconds(plugin.getConfig().getInt("features.title.fade-in"));
            Duration stay = Duration.ofSeconds(plugin.getConfig().getInt("features.title.stay"));
            Duration fadeOut = Duration.ofSeconds(plugin.getConfig().getInt("features.title.fade-out"));

            player.showTitle(net.kyori.adventure.title.Title.title(
                    net.kyori.adventure.text.Component.text(title),
                    net.kyori.adventure.text.Component.text(subtitle),
                    net.kyori.adventure.title.Title.Times.times(fadeIn, stay, fadeOut)
            ));
        }
    }


    private void playSound(Player player) {
        if (config.getBoolean("features.sound.enabled")) {
            String soundName = plugin.getConfig().getString("features.sound.sound", "ENTITY_EXPERIENCE_ORB_PICKUP");

            String resolvedSoundName = resolveSoundName(soundName);

            try {
                float volume = (float) plugin.getConfig().getDouble("features.sound.volume", 1.0);
                float pitch = (float) plugin.getConfig().getDouble("features.sound.pitch", 1.0);

                player.playSound(player.getLocation(), resolvedSoundName, SoundCategory.MASTER, volume, pitch);
            } catch (Exception e) {
                plugin.getLogger().warning("Invalid sound specified in config: " + soundName);
            }
        }
    }
    private String resolveSoundName(String soundName) {
        return switch (soundName.toUpperCase()) {
            case "ENTITY_EXPERIENCE_ORB_PICKUP" -> "minecraft:entity.experience_orb.pickup";
            case "BLOCK_ANVIL_PLACE" -> "minecraft:block.anvil.place";
            case "ITEM_TRIDENT_THROW" -> "minecraft:item.trident.throw";
            default -> soundName.toLowerCase();
        };
    }

    public static String translateLegacyColorCodes(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

}
