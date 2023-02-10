package edu.whimc.overworld_agent.commands.subcommands;

import edu.whimc.overworld_agent.OverworldAgent;
import edu.whimc.overworld_agent.commands.AbstractSubCommand;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Class to define command for despawning agents
 * @author sam
 */
public class SpawnAgentsCommand extends AbstractSubCommand {
    private static final String ALL = "all";

    public SpawnAgentsCommand(OverworldAgent plugin, String baseCommand, String subCommand){
        super(plugin, baseCommand, subCommand);
        super.description("Spawns specified player's agent (all for all players)");
        super.arguments("agentName");
    }

    @Override
    protected boolean onCommand(CommandSender sender, String[] args) {

        Map<String,NPC> npcs = plugin.getAgents();
        if (args.length < 1) {
            sender.sendMessage("No player name was given");
            return true;
        }
        String playerName = args[0];
        if (playerName.equalsIgnoreCase(ALL)) {
            for (Map.Entry<String, NPC> entry : npcs.entrySet()) {
                NPC npc = entry.getValue();
                String currName = entry.getKey();
                if (Bukkit.getPlayer(currName) != null) {
                    npc.spawn(Bukkit.getPlayer(currName).getLocation());
                }
            }
            sender.sendMessage("All agents were reactivated");
        } else {
            if(Bukkit.getPlayer(playerName) != null){
                NPC npc = npcs.get(playerName);
                if(npc != null) {
                    npc.spawn(Bukkit.getPlayer(playerName).getLocation());
                    sender.sendMessage(npc.getName() + " was spawned");
                } else {
                    sender.sendMessage("Player does not have an agent");
                }
            }
        }
        return true;
    }

    @Override
    protected List<java.lang.String> onTabComplete(CommandSender sender, java.lang.String[] args) {
        List<String> list = new ArrayList<String>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            list.add(p.getName());
        }
        list.add("all");
        return list;
    }

}