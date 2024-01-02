package edu.whimc.overworld_agent.commands.subcommands;

import edu.whimc.overworld_agent.OverworldAgent;
import edu.whimc.overworld_agent.commands.AbstractSubCommand;

import edu.whimc.overworld_agent.dialoguetemplate.models.DialogueType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChangeAgentTypeCommand extends AbstractSubCommand {
    private final String COMMAND = "agent_type";

    public ChangeAgentTypeCommand(OverworldAgent plugin, String baseCommand, String subCommand){
        super(plugin, baseCommand, subCommand);
        super.description("Changes agent type for dialogue type");
        super.arguments("type");
    }
    /**
     * Creates a dialogue menu to chat with the agent
     * @param sender - Source of the command
     * @param args - Passed command arguments
     * @return if the command was successfully executed
     */
    @Override
    protected boolean onCommand(CommandSender sender, String[] args) {
        Player player;
        boolean text = true;
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player");
            return true;
        } else {
            player = (Player) sender;
        }
        if (args.length < 1) {
            sender.sendMessage("No agent type was given");
            return true;
        }
        String agentType = args[0];
        for (DialogueType type : DialogueType.class.getEnumConstants()) {
            if(type.toString().equalsIgnoreCase(agentType)) {
                plugin.setAgentType(type);
                sender.sendMessage("Agent type set to " + agentType);
                return true;
            }
        }
        sender.sendMessage("Agent type not valid");
        return true;
    }

    @Override
    protected List<String> onTabComplete(CommandSender sender, java.lang.String[] args) {
        List<String> list = new ArrayList<String>();
        for (DialogueType type : Arrays.asList(DialogueType.class.getEnumConstants())) {
            list.add(type.toString());
        }
        return list;
    }
}
