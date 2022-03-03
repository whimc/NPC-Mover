package edu.whimc.NPCMover.commands;


import edu.whimc.NPCMover.traits.SpawnNoviceTrait;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.trait.LookClose;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
/**
 * Class to define command for spawning an expert agent
 * @author sam
 */
public class NoviceSpawnCommand implements CommandExecutor, TabCompleter {

    /**
     * Creates a new novice agent and adds the entity to the world with the appropriate traits
     * @param sender - Source of the command
     * @param command - Command which was executed
     * @param label - Alias of the command which was used
     * @param args - Passed command arguments
     * @return if the command was successfully executed
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        //NPC name sent in as first argument of MC command
        String npcName = args[0];
        NPCRegistry registry = CitizensAPI.getNPCRegistry();

        //NPC is a player and follows the sender (Need to change to follow specified player name) and has behaviors specified in SpawnNoviceTrait
        NPC npc = registry.createNPC(EntityType.PLAYER, npcName);
        npc.getOrAddTrait(LookClose.class).lookClose(true);
        npc.addTrait(SpawnNoviceTrait.class);

        //Spawn at location of sender
        npc.spawn(player.getLocation());
        return true;
    }

    /**
     * Allows tab completion of command
     * @param sender - Source of the command
     * @param command - Command which was executed
     * @param alias - Alias of the command which was used
     * @param args - Passed command arguments
     * @return list of tab completions (currently empty)
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // Don't show any tab completions
        return Arrays.asList();
    }
}

