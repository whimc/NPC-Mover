package edu.whimc.overworld_agent.commands;


import edu.whimc.overworld_agent.OverworldAgent;
import edu.whimc.overworld_agent.traits.SpawnNoviceTrait;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
    private OverworldAgent plugin;

    public NoviceSpawnCommand(OverworldAgent plugin){
        this.plugin = plugin;
    }

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

        //Player name first argument, skin name 2nd, NPC 3rd
        String playerName = args[0];
        String skinName = args[1];
        String npcName = args[2];

        Player player = Bukkit.getPlayer(playerName);

        NPCRegistry registry = CitizensAPI.getNPCRegistry();
        //NPC is a player and guides the assigned player and has behaviors specified in SpawnNoviceTrait
        NPC npc = registry.createNPC(EntityType.PLAYER, npcName);
        npc.getOrAddTrait(LookClose.class).lookClose(true);

        SpawnNoviceTrait trait = new SpawnNoviceTrait();
        trait.setPlayer(player);
        npc.addTrait(trait);

        //Set NPC skin by grabbing values from config
        String signature = plugin.getConfig().getString("skins."+skinName+".signature");
        String data = plugin.getConfig().getString("skins."+skinName+".data");
        SkinTrait skinTrait = npc.getOrAddTrait(SkinTrait.class);
        skinTrait.setSkinPersistent(skinName, signature, data);

        plugin.getQueryer().storeNewAgent(player, skinName, npcName, id -> {
            //Spawn at location of sender
            npc.spawn(player.getLocation());
            plugin.getAgents().add(npc);
        });
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

