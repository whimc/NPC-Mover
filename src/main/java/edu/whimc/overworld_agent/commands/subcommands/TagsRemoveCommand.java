package edu.whimc.overworld_agent.commands.subcommands;

import edu.whimc.overworld_agent.OverworldAgent;
import edu.whimc.overworld_agent.commands.AbstractSubCommand;
import edu.whimc.overworld_agent.utils.Utils;
import edu.whimc.overworld_agent.dialoguetemplate.Tag;
import org.bukkit.command.CommandSender;

import java.util.List;

public class TagsRemoveCommand extends AbstractSubCommand {

    public TagsRemoveCommand(OverworldAgent plugin, String baseCommand, String subCommand) {
        super(plugin, baseCommand, subCommand);
        super.description("Removes an tag");
        super.arguments("id");
    }

    @Override
    protected boolean onCommand(CommandSender sender, String[] args) {
        Tag tag = Utils.getTagWithError(sender, args[0]);
        if (tag == null) return true;

        tag.deleteAndSetInactive(() -> {
            Utils.msg(sender, "&aTag \"&2" + tag.getId() + "&a\" removed!");
        });

        return true;
    }

    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        return Tag.getTagsTabComplete(args[0]);
    }
}
