package edu.whimc.overworld_agent.commands;

import edu.whimc.overworld_agent.OverworldAgent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class to define command for despawning agents
 * @author sam
 */
public class DespawnAgentsCommand implements CommandExecutor, TabCompleter {
    private OverworldAgent plugin;
    public static final String SPAWN_PERM = OverworldAgent.PERM_PREFIX + ".despawn";
    public DespawnAgentsCommand(OverworldAgent plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!commandSender.hasPermission(SPAWN_PERM)) {
            commandSender.sendMessage(
                    "You do not have the required permission!");
            return true;
        }
        Map<String,NPC> npcs = plugin.getAgents();

        for (Map.Entry<String,NPC> entry : npcs.entrySet()){
            NPC npc = entry.getValue();
            npc.despawn();
        }
        plugin.removeAgents();
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
