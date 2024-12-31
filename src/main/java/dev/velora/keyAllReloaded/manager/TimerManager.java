package dev.velora.keyAllReloaded.manager;

import dev.velora.keyAllReloaded.KeyAllReloaded;
import dev.velora.keyAllReloaded.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

@SuppressWarnings("SameParameterValue")
public class TimerManager {

    private final KeyAllReloaded plugin;
    private BukkitTask timerTask;
    private int remainingTime;
    private int initialTime;
    private Consumer<Integer> onUpdate;
    private Runnable onEnd;

    private boolean debug;

    public TimerManager(KeyAllReloaded plugin) {
        this.plugin = plugin;
        this.timerTask = null;
        this.onUpdate = null;
        this.onEnd = null;
        reloadDebugMode();
    }

    /**
     * Reloads the debug mode from the configuration.
     */
    public void reloadDebugMode() {
        FileConfiguration config = plugin.getConfig();
        this.debug = config.getBoolean("debug", false);
    }

    /**
     * Starts the timer with the specified duration and interval.
     *
     * @param durationInSeconds The total duration of the timer in seconds.
     * @param intervalInTicks   The interval at which the timer updates, in ticks (20 ticks = 1 second).
     * @param onUpdate          A callback that gets called on each update with the remaining time.
     * @param onEnd             A callback that gets called when the timer ends.
     */
    public void startTimer(int durationInSeconds, int intervalInTicks, Consumer<Integer> onUpdate, Runnable onEnd) {
        if (timerTask != null) {
            stopTimer();
        }

        this.remainingTime = durationInSeconds;
        this.initialTime = durationInSeconds;
        this.onUpdate = onUpdate;
        this.onEnd = onEnd;

        timerTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (onUpdate != null) {
                onUpdate.accept(remainingTime);
            }

            if (remainingTime <= 0) {
                stopTimer();
                if (onEnd != null) {
                    onEnd.run();
                }
                reloadConfigAndRestartTimer();
            } else {
                remainingTime--;
            }
        }, 0L, intervalInTicks);
    }

    /**
     * Simplified method to start a timer that executes commands for each online player when finished.
     *
     * @param durationInSeconds The total duration of the timer in seconds.
     */
    public void startTimerWithCommandsForPlayers(int durationInSeconds) {
        FileConfiguration config = plugin.getConfig();
        List<String> commands = config.getStringList("timer.commands");

        startTimer(durationInSeconds, 20, remainingTime -> {
            if (debug) {
                plugin.getLogger().info("Time remaining: " + formatTime(remainingTime));
            }
        }, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                executeCommandsWithDelay(player, commands);
            }
        });
    }

    /**
     * Executes a list of commands for a player with a small delay between each command.
     *
     * @param player   The player for whom the commands are executed.
     * @param commands The list of commands to execute.
     */
    private void executeCommandsWithDelay(Player player, List<String> commands) {
        new BukkitRunnable() {
            private int index = 0;

            @Override
            public void run() {
                if (index >= commands.size()) {
                    cancel();
                    return;
                }

                String command = commands.get(index).replace("%player%", player.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                new Utils(plugin).executeExternalFeatures(player, player);

                if (debug) {
                    plugin.getLogger().info("Command executed: " + command);
                }

                index++;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    /**
     * Reloads the configuration and restarts the timer with updated values.
     */
    public void reloadConfigAndRestartTimer() {
        try {
            plugin.reloadConfig();
            FileConfiguration config = plugin.getConfig();

            reloadDebugMode();

            boolean randomTimerEnabled = getConfigBoolean(config, "random-timer.enabled", false);

            if (randomTimerEnabled) {
                int minTime = getConfigInt(config, "random-timer.min", 300);
                int maxTime = getConfigInt(config, "random-timer.max", 1200);

                if (minTime > maxTime) {
                    plugin.getLogger().severe("Configuration error: 'random-timer.min' is greater than 'random-timer.max'. Using default values.");
                    minTime = 300;
                    maxTime = 1200;
                }

                int randomDuration = getRandomTime(minTime, maxTime);

                startTimerWithCommandsForPlayers(randomDuration);

                if (debug) {
                    plugin.getLogger().info("Random timer started with duration: " + randomDuration + " seconds.");
                }
            } else {
                int durationInSeconds = getConfigInt(config, "timer.duration", 600);
                startTimerWithCommandsForPlayers(durationInSeconds);

                if (debug) {
                    plugin.getLogger().info("Timer started with duration: " + durationInSeconds + " seconds.");
                }
            }
        } catch (Exception e) {
            plugin.getLogger().severe("An error occurred while reloading the configuration: " + e.getMessage());
            for (StackTraceElement element : e.getStackTrace()) {
                plugin.getLogger().severe("    at " + element.toString());
            }
        }
    }

    /**
     * Safely retrieves a boolean value from the configuration, logging an error if the key is missing or invalid.
     *
     * @param config The configuration to read from.
     * @param path   The path to the boolean value.
     * @param def    The default value to return if the key is missing or invalid.
     * @return The boolean value or the default if an error occurs.
     */
    private boolean getConfigBoolean(FileConfiguration config, String path, boolean def) {
        try {
            return config.getBoolean(path, def);
        } catch (Exception e) {
            plugin.getLogger().severe("Configuration error: Unable to read boolean value for path '" + path + "'. Using default value: " + def);
            return def;
        }
    }

    /**
     * Safely retrieves an integer value from the configuration, logging an error if the key is missing or invalid.
     *
     * @param config The configuration to read from.
     * @param path   The path to the integer value.
     * @param def    The default value to return if the key is missing or invalid.
     * @return The integer value or the default if an error occurs.
     */
    private int getConfigInt(FileConfiguration config, String path, int def) {
        try {
            return config.getInt(path, def);
        } catch (Exception e) {
            plugin.getLogger().severe("Configuration error: Unable to read integer value for path '" + path + "'. Using default value: " + def);
            return def;
        }
    }

    /**
     * Stops the timer if it's running.
     */
    public void stopTimer() {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    /**
     * Resets the timer to its initial duration without restarting it.
     */
    public void resetTimer() {
        stopTimer();
        this.remainingTime = initialTime;
    }

    /**
     * Gets the remaining time in seconds.
     *
     * @return The remaining time.
     */
    public int getRemainingTime() {
        return remainingTime;
    }

    /**
     * Checks if the timer is currently running.
     *
     * @return True if the timer is running, false otherwise.
     */
    public boolean isTimerRunning() {
        return timerTask != null;
    }

    /**
     * Utility method to format time in seconds dynamically, removing leading zero fields.
     *
     * @param timeInSeconds The time in seconds.
     * @return A formatted time string with only non-zero fields.
     */
    public static String formatTime(int timeInSeconds) {
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

    /**
     * Generates a random time between the specified minimum and maximum values.
     *
     * @param min The minimum time in seconds.
     * @param max The maximum time in seconds.
     * @return A random time in seconds between min and max.
     */
    private int getRandomTime(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }
}
