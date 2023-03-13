package edu.whimc.overworld_agent.dialoguetemplate.runnables;

import edu.whimc.overworld_agent.OverworldAgent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.trait.FollowTrait;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class RebuildRunnable implements Runnable{
    private OverworldAgent plugin;
    private List<String[]> lookup;
    private int step;
    private Player sender;
    private NPC npc;
    private Location npcStartingLocation;
    private long endTime;
    private static final int SECTOTICK = 20;
    private static final int MILLITOSEC = 1000;
    public RebuildRunnable(OverworldAgent plugin, Player sender, NPC npc, Location npcStartingLocation, List<String[]> lookup, int step, long endTime){
        this.plugin = plugin;
        this.lookup = lookup;
        this.step = step;
        this.sender = sender;
         this.npc = npc;
         this.npcStartingLocation = npcStartingLocation;
         this.endTime = endTime;
    }

    @Override
    public void run() {
        CoreProtectAPI api = this.getCoreProtect();
        CoreProtectAPI.ParseResult result = api.parseResult(lookup.get(step));
        if(result.getTimestamp() <= endTime) {
            CoreProtectAPI.ParseResult first = api.parseResult(lookup.get(0));
            Location startingLoc = new Location(sender.getWorld(), first.getX(), first.getY(), first.getZ());
            long currTime = result.getTimestamp();

            Material material = result.getType();
            Location location = new Location(sender.getWorld(), result.getX(), result.getY(), result.getZ());
            //(0=removed, 1=placed, 2=interaction)
            int action = result.getActionId();
            Location adjustedLocation = new Location(sender.getWorld(), npcStartingLocation.getX() - (startingLoc.getX() - location.getX()), npcStartingLocation.getY() - (startingLoc.getY() - location.getY()), npcStartingLocation.getZ() - (startingLoc.getZ() - location.getZ()));
            new BukkitRunnable() {
                public void run() {
                    if (action == 0) {
                        adjustedLocation.getBlock().setType(Material.AIR);
                    } else if (action == 1) {
                        Equipment ee = npc.getTrait(Equipment.class);
                        ee.set(Equipment.EquipmentSlot.HAND, new ItemStack(material, 1));
                        adjustedLocation.getBlock().setType(material);
                    }
                    step++;
                    if (step < lookup.size()) {
                        CoreProtectAPI.ParseResult next = api.parseResult(lookup.get(step));
                        long time = Math.abs(Math.round(SECTOTICK * (next.getTimestamp() - currTime) / MILLITOSEC));
                        Location locationNext = new Location(sender.getWorld(), next.getX(), next.getY(), next.getZ());
                        npc.getNavigator().setTarget(new Location(sender.getWorld(), npcStartingLocation.getX() - (startingLoc.getX() - locationNext.getX()), npcStartingLocation.getY() - (startingLoc.getY() - locationNext.getY()), npcStartingLocation.getZ() - (startingLoc.getZ() - locationNext.getZ())));
                        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, new RebuildRunnable(plugin, sender, npc, npcStartingLocation, lookup, step, endTime), time);
                    }  else {
                        if(!npc.getOrAddTrait(FollowTrait.class).isActive()) {
                            npc.getOrAddTrait(FollowTrait.class).toggle(sender, false);
                        }
                        sender.sendMessage("The build is complete");
                    }
                }
            }.runTask(plugin);
        } else {
            if(!npc.getOrAddTrait(FollowTrait.class).isActive()) {
                npc.getOrAddTrait(FollowTrait.class).toggle(sender, false);
            }
            sender.sendMessage("The build is complete");
        }
    }
    private CoreProtectAPI getCoreProtect() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("CoreProtect");

        // Check that CoreProtect is loaded
        if (plugin == null || !(plugin instanceof CoreProtect)) {
            return null;
        }

        // Check that the API is enabled
        CoreProtectAPI CoreProtect = ((CoreProtect) plugin).getAPI();
        if (CoreProtect.isEnabled() == false) {
            return null;
        }

        // Check that a compatible version of the API is loaded
        if (CoreProtect.APIVersion() < 8) {
            return null;
        }

        return CoreProtect;
    }
}
