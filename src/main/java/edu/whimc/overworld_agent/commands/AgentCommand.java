package edu.whimc.overworld_agent.commands;

import edu.whimc.overworld_agent.OverworldAgent;
import edu.whimc.overworld_agent.commands.subcommands.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AgentCommand implements CommandExecutor, TabCompleter {

    private final Map<String, AbstractSubCommand> subCommands = new HashMap<>();

    public AgentCommand(OverworldAgent plugin) {
        subCommands.put("despawn", new DespawnAgentsCommand(plugin, "agent", "despawn"));
        subCommands.put("destroy", new DestroyAgentsCommand(plugin, "agent", "destroy"));
        subCommands.put("spawn", new ExpertSpawnCommand(plugin, "agent", "spawn"));
        //subCommands.put("speechspawn", new SpeechSpawnCommand(plugin, "agent", "speechspawn"));
        subCommands.put("rebuilderspawn", new RebuilderSpawnCommand(plugin, "agent", "rebuilderspawn"));
        subCommands.put("reactivate", new SpawnAgentsCommand(plugin, "agent", "reactivate"));
        subCommands.put("skin_type", new SkinTypeCommand(plugin, "agent", "skin_type"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("You need to add another argument. Please try again");
            return true;
        }

        AbstractSubCommand subCmd = subCommands.getOrDefault(args[0].toLowerCase(), null);
        if (subCmd == null) {
            sender.sendMessage("You need to add another argument. Please try again");
            return true;
        }

        return subCmd.executeSubCommand(sender, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 0) {
            return subCommands.keySet().stream().sorted().collect(Collectors.toList());
        }

        if (args.length == 1) {
            return subCommands.keySet()
                    .stream()
                    .filter(v -> v.startsWith(args[0].toLowerCase()))
                    .sorted()
                    .collect(Collectors.toList());
        }

        AbstractSubCommand subCmd = subCommands.getOrDefault(args[0].toLowerCase(), null);
        if (subCmd == null) {
            return null;
        }

        return subCmd.executeOnTabComplete(sender, Arrays.copyOfRange(args, 1, args.length));
    }


}

