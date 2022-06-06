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

/**
 * Class to define command for despawning agents
 * @author sam
 */
public class DespawnAgentsCommand implements CommandExecutor, TabCompleter {
    private OverworldAgent plugin;
    public static final String SPAWN_PERM = OverworldAgent.PERM_PREFIX + ".spawn";
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
        ArrayList<NPC> npcs = plugin.getAgents();

        for(int k = 0; k < npcs.size(); k++){
            NPC npc = npcs.get(k);
            npc.destroy();
        }
        plugin.removeAgents();
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
