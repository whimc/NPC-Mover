package edu.whimc.overworld_agent.commands;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import edu.whimc.overworld_agent.OverworldAgent;
import edu.whimc.overworld_agent.dialoguetemplate.Interaction;
import edu.whimc.overworld_agent.dialoguetemplate.events.BuildAssessEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class HabitatAssessCommand implements CommandExecutor, TabCompleter {
    private OverworldAgent plugin;
    private Logger log;

    public HabitatAssessCommand(OverworldAgent plugin) {
        this.plugin = plugin;
        log = Logger.getLogger("Minecraft");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("You need to add another argument. Please try again");
            return true;
        }
        String playerName = args[0];
        if(Bukkit.getPlayer(playerName) != null) {
            Player player = Bukkit.getPlayer(playerName);
            this.plugin.getQueryer().storeNewBuildInteraction(new Interaction(plugin, player, "assess"), -1, id -> {
                Set<UUID> teammates;
                RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                RegionManager regionManager = container.get(BukkitAdapter.adapt(player).getWorld());
                Map<String, ProtectedRegion> regions = regionManager.getRegions();
                if(regions != null){
                    for (Map.Entry<String,ProtectedRegion> region : regions.entrySet())  {
                        ProtectedRegion buildArea = region.getValue();
                        DefaultDomain members = buildArea.getMembers();
                        if(members.contains(player.getUniqueId())){
                            teammates = members.getUniqueIds();
                            BuildAssessEvent assess = new BuildAssessEvent(player, id, teammates);
                            Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getServer().getPluginManager().callEvent(assess));
                            break;
                        }
                    }
                } else {
                    log.info("There are no regions on this map");
                    player.sendMessage("There are no regions on this world. Please ask an admin to help you set up your base!");
                }
            });
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list = new ArrayList<String>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            list.add(p.getName());
        }
        return list;
    }


}