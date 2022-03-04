package edu.whimc.NPCMover.commands;

import edu.whimc.NPCMover.NPCMover;
import edu.whimc.NPCMover.traits.SpawnExpertTrait;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.trait.FollowTrait;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Bukkit;
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
public class ExpertSpawnCommand implements CommandExecutor, TabCompleter {

    /**
     * Creates a new expert agent and adds the entity to the world with the appropriate traits
     * @param sender - Source of the command
     * @param command - Command which was executed
     * @param label - Alias of the command which was used
     * @param args - Passed command arguments
     * @return if the command was successfully executed
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        NPCMover plugin = NPCMover.getInstance();
        //Player name first argument, skin name 2nd, NPC 3rd
        String playerName = args[0];
        String skinName = args[1];
        String npcName = args[2];

        Player player = Bukkit.getPlayer(playerName);

        NPCRegistry registry = CitizensAPI.getNPCRegistry();

        //NPC is a player and follows the assigned player and has behaviors specified in SpawnExpertTrait
        NPC npc = registry.createNPC(EntityType.PLAYER, npcName);
        npc.getOrAddTrait(FollowTrait.class).toggle(player,false);
        SpawnExpertTrait trait = new SpawnExpertTrait();

        trait.setPlayer(player);
        npc.addTrait(trait);

        //Set NPC skin by grabbing values from config
        String signature = plugin.getConfig().getString("skins."+skinName+".signature");
        String data = plugin.getConfig().getString("skins."+skinName+".data");
        SkinTrait skinTrait = npc.getOrAddTrait(SkinTrait.class);
        skinTrait.setSkinPersistent(skinName, signature, data);

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
