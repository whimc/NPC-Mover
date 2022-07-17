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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChangeAgentNameCommand  extends AbstractSubCommand {
    public static final String EDIT_PERM = OverworldAgent.PERM_PREFIX + ".spawn";
    private final String COMMAND = "edit";

    public ChangeAgentNameCommand(OverworldAgent plugin, String baseCommand, String subCommand){
        super(plugin, baseCommand, subCommand);
        super.description("Changes name of the sender's agent");
        super.arguments("agentName");
    }
    @Override
    protected boolean onCommand(CommandSender sender, String[] args) {
        Player player;
        String playerName = "";
        String agentName = "";
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
            if(args.length == 0){
                player.sendMessage("You need to enter an agent name. Please try again");
                return true;
            } else {
                for(int k = 0; k < args.length; k++){
                    agentName += args[k] + " ";
                }
            }
        }
        Map<String,NPC> npcs = plugin.getAgents();
        NPC npc = npcs.get(playerName);

        if(npc != null) {
            npc.setName(agentName);
            plugin.getQueryer().storeNewAgent(player, COMMAND, agentName, npc.getOrAddTrait(SkinTrait.class).getSkinName(), id -> {
                plugin.getAgents().put(player.getName(), npc);
            });
        } else {
            player.sendMessage("You need to have an agent. Please try again");
        }
        return true;
    }

}
