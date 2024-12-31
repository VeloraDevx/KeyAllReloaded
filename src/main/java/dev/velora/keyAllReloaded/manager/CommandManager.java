package dev.velora.keyAllReloaded.manager;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Command Manager
 */
public class CommandManager {
    private final JavaPlugin plugin;

    public CommandManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerCommands(Object commandHolder) {
        for (var method : commandHolder.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(CommandInfo.class)) {
                CommandInfo info = method.getAnnotation(CommandInfo.class);
                registerCommand(info, commandHolder, method);
            }
        }
    }

    private void registerCommand(CommandInfo info, Object holder, java.lang.reflect.Method method) {
        String name = info.name();
        String description = info.description().isEmpty() ? "No description provided" : info.description();
        String usage = info.usage().isEmpty() ? "/" + name : info.usage();
        String[] aliases = info.aliases();

        Command command = new Command(name, description, usage, List.of(aliases)) {
            @Override
            public boolean execute(CommandSender sender, String label, String[] args) {
                try {
                    if (!info.permission().isEmpty() && !sender.hasPermission(info.permission())) {
                        sender.sendMessage("\u00a7cYou do not have permission to use this command.");
                        return true;
                    }

                    // Execute the command
                    if (info.async()) {
                        CompletableFuture.runAsync(() -> invokeCommand(holder, method, sender, args));
                    } else {
                        invokeCommand(holder, method, sender, args);
                    }
                } catch (Exception e) {
                    sender.sendMessage("\u00a7cAn error occurred while executing the command. Please contact an administrator.");
                    e.printStackTrace();
                }
                return true;
            }

            @Override
            public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
                if (!info.permission().isEmpty() && !sender.hasPermission(info.permission())) {
                    return Collections.emptyList();
                }

                // Handle tab completions
                try {
                    if (!info.tabCompletionMethod().isEmpty()) {
                        var tabMethod = holder.getClass().getDeclaredMethod(info.tabCompletionMethod(), CommandSender.class, String[].class);
                        tabMethod.setAccessible(true);
                        Object result = tabMethod.invoke(holder, sender, args);
                        if (result instanceof List) {
                            return (List<String>) result;
                        }
                    }
                } catch (NoSuchMethodException ignored) {
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return args.length > 0 && args[args.length - 1].equalsIgnoreCase("@online")
                        ? getOnlinePlayerNames(args[args.length - 1])
                        : getStaticTabCompletions(info, args);
            }
        };

        registerToCommandMap(command);
    }

    private void invokeCommand(Object holder, java.lang.reflect.Method method, CommandSender sender, String[] args) {
        try {
            method.setAccessible(true);
            method.invoke(holder, sender, args);
        } catch (Exception e) {
            sender.sendMessage("\u00a7cAn internal error occurred while executing the command.");
            e.printStackTrace();
        }
    }

    private List<String> getStaticTabCompletions(CommandInfo info, String[] args) {
        List<String> completions = new ArrayList<>();

        String[] tabHints = info.tabComplete();
        if (args.length <= tabHints.length) {
            String hint = tabHints[args.length - 1];
            if (!hint.isEmpty()) {
                for (String option : hint.split("\\|")) {
                    if (option.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                        completions.add(option);
                    }
                }
            }
        }

        return completions;
    }

    private List<String> getOnlinePlayerNames(String input) {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase().startsWith(input.toLowerCase()))
                .collect(Collectors.toList());
    }

    private void registerToCommandMap(Command command) {
        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);

            SimpleCommandMap commandMap = (SimpleCommandMap) commandMapField.get(Bukkit.getServer());
            commandMap.register(plugin.getDescription().getName(), command);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}