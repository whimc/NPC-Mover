package edu.whimc.overworld_agent.commands.subcommands;

import edu.whimc.overworld_agent.OverworldAgent;
import edu.whimc.overworld_agent.commands.AbstractSubCommand;
import edu.whimc.overworld_agent.traits.SpawnExpertTrait;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.trait.FollowTrait;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Class to define command for spawning an expert agent
 * @author sam
 */
public class SkinTypeCommand extends AbstractSubCommand {


    private final String COMMAND = "skintype";

    public SkinTypeCommand(OverworldAgent plugin, String baseCommand, String subCommand){
        super(plugin, baseCommand, subCommand);
        super.description("Changes skin type for agents");
        super.arguments("skinType");
    }
    /**
     * Creates a new expert agent and adds the entity to the world with the appropriate traits
     * @param sender - Source of the command
     * @param args - Passed command arguments
     * @return if the command was successfully executed
     */
    @Override
    protected boolean onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player");
            return true;
        } else if (args.length != 1){
            sender.sendMessage("Please enter just the skin type for the agents!");
            return true;
        }
        String skinType = args[0];
        ConfigurationSection sec = plugin.getConfig().getConfigurationSection("skins");
        Set<String> keys = sec.getKeys(false);
        if(keys.contains(skinType)){
            plugin.setSkinType(skinType);
            sender.sendMessage("Skin type changed to " + skinType);
        } else {
            sender.sendMessage(skinType + " skin type does not exist!");
        }
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
