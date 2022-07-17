package edu.whimc.overworld_agent.commands.subcommands;

import edu.whimc.overworld_agent.OverworldAgent;
import edu.whimc.overworld_agent.commands.AbstractSubCommand;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

public class ChangeAgentSkinCommand  extends AbstractSubCommand {
    public static final String EDIT_PERM = OverworldAgent.PERM_PREFIX + ".spawn";
    private final String COMMAND = "edit";

    public ChangeAgentSkinCommand(OverworldAgent plugin, String baseCommand, String subCommand){
        super(plugin, baseCommand, subCommand);
        super.description("Changes skin of the sender's agent");
        super.arguments("agentSkin");
    }

    @Override
    protected boolean onCommand(CommandSender sender, String[] args) {
        Player player;
        String playerName = "";
        String skinName = "";
        if (!sender.hasPermission(EDIT_PERM)) {
            sender.sendMessage(
                    "You do not have the required permission!");
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player");
            return true;
        } else {
            player = (Player) sender;
            playerName = player.getName();
            if(args.length != 1){
                player.sendMessage("You need to enter an agent skin. Please try again");
                return true;
            } else {
                skinName = args[0];
            }
        }
        Map<String,NPC> npcs = plugin.getAgents();
        NPC npc = npcs.get(playerName);
        ConfigurationSection sec = plugin.getConfig().getConfigurationSection("skins");
        Set<String> keys = sec.getKeys(false);
        List<String> skins = new ArrayList<>(keys);

        if(npc != null) {
            if (!skins.contains(skinName)) {
                player.sendMessage("You did not enter a correct skin name");
                return false;
            }
            //Set NPC skin by grabbing values from config
            String signature = plugin.getConfig().getString("skins." + skinName + ".signature");
            String data = plugin.getConfig().getString("skins." + skinName + ".data");
            SkinTrait skinTrait = npc.getOrAddTrait(SkinTrait.class);
            skinTrait.setSkinPersistent(skinName, signature, data);
            plugin.getQueryer().storeNewAgent(player, COMMAND, npc.getName(), skinName, id -> {
                plugin.getAgents().put(player.getName(), npc);
            });
        } else {
            player.sendMessage("You need to have an agent. Please try again");
        }
        return true;
    }

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
