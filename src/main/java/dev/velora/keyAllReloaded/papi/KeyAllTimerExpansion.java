package dev.velora.keyAllReloaded.papi;

import dev.velora.keyAllReloaded.manager.TimerManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class KeyAllTimerExpansion extends PlaceholderExpansion {

    private final TimerManager timerManager;

    public KeyAllTimerExpansion(TimerManager timerManager) {
        this.timerManager = timerManager;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "keyall";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Ammar";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (identifier.equalsIgnoreCase("timer")) {
            if (timerManager.isTimerRunning()) {
                return formatDynamicTime(timerManager.getRemainingTime());
            } else {
                return "Timer not running";
            }
        }
        return null;
    }

    /**
     * Formats time dynamically by removing leading zero fields.
     *
     * @param timeInSeconds The time in seconds.
     * @return A formatted string representing the remaining time.
     */
    private String formatDynamicTime(int timeInSeconds) {
        int days = timeInSeconds / 86400;
        int hours = (timeInSeconds % 86400) / 3600;
        int minutes = (timeInSeconds % 3600) / 60;
        int seconds = timeInSeconds % 60;

        if (days > 0) {
            return String.format("%d:%02d:%02d:%02d", days, hours, minutes, seconds);
        } else if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%d:%02d", minutes, seconds);
        }
    }
}
