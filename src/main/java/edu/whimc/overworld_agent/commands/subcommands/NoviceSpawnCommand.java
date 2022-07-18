package edu.whimc.overworld_agent.commands.subcommands;


import edu.whimc.overworld_agent.OverworldAgent;
import edu.whimc.overworld_agent.commands.AbstractSubCommand;
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
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Class to define command for spawning an expert agent
 * @author sam
 */
public class NoviceSpawnCommand extends AbstractSubCommand {
    public static final String SPAWN_PERM = OverworldAgent.PERM_PREFIX + ".spawn";
    private final String COMMAND = "novice";

    public NoviceSpawnCommand(OverworldAgent plugin, String baseCommand, String subCommand){
        super(plugin, baseCommand, subCommand);
        super.description("Changes name of the sender's agent");
        super.arguments("agentName");
    }

    /**
     * Creates a new novice agent and adds the entity to the world with the appropriate traits
     * @param sender - Source of the command
     * @param args - Passed command arguments
     * @return if the command was successfully executed
     */
    @Override
    protected boolean onCommand(CommandSender sender, String[] args) {
        //Skin name 1st, NPC 2nd
        String skinName = args[0];
        String npcName = "";
        String playerName = "";
        Player player;
        for(int k = 1; k < args.length; k++){
            npcName += args[k] + " ";
        }


        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player");
            return true;
        } else {
            player = (Player) sender;
            playerName = player.getName();
        }

        if (!sender.hasPermission(SPAWN_PERM)) {
            player.sendMessage(
                    "You do not have the required permission!");
            return true;
        }

        if(!plugin.getAgents().containsKey(playerName)) {

            ConfigurationSection sec = plugin.getConfig().getConfigurationSection("skins");
            Set<String> keys = sec.getKeys(false);
            List<String> skins = new ArrayList<>(keys);
            if (!skins.contains(skinName)) {
                player.sendMessage("You did not enter a correct skin name");
                return false;
            }

            NPCRegistry registry = CitizensAPI.getNPCRegistry();
            //NPC is a player and guides the assigned player and has behaviors specified in SpawnNoviceTrait
            NPC npc = registry.createNPC(EntityType.PLAYER, npcName);
            npc.getOrAddTrait(LookClose.class).lookClose(true);

            SpawnNoviceTrait trait = new SpawnNoviceTrait();
            trait.setPlayer(player);
            npc.addTrait(trait);

            //Set NPC skin by grabbing values from config
            String signature = plugin.getConfig().getString("skins." + skinName + ".signature");
            String data = plugin.getConfig().getString("skins." + skinName + ".data");
            SkinTrait skinTrait = npc.getOrAddTrait(SkinTrait.class);
            skinTrait.setSkinPersistent(skinName, signature, data);
            plugin.getQueryer().storeNewAgent(player, COMMAND, npcName, skinName, id -> {
                npc.spawn(player.getLocation());
                plugin.getAgents().put(player.getName(), npc);
            });
            return true;
        }
        player.sendMessage("You already have an agent");
        return true;
    }

    /**
     * Allows tab completion of command
     * @param sender - Source of the command
     * @param args - Passed command arguments
     * @return list of tab completions (currently empty)
     */
    @Override
    protected List<java.lang.String> onTabComplete(CommandSender sender, java.lang.String[] args) {
        if (args.length == 1) {
            ConfigurationSection sec = plugin.getConfig().getConfigurationSection("skins");
            Set<String> keys = sec.getKeys(false);
            List<String> skins = new ArrayList<>(keys);
            return skins;
        }
        return Arrays.asList();
    }
}

