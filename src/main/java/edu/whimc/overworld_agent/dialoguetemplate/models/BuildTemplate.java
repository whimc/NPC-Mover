package edu.whimc.overworld_agent.dialoguetemplate.models;

import edu.whimc.overworld_agent.OverworldAgent;
import edu.whimc.overworld_agent.dialoguetemplate.runnables.RebuildRunnable;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.FollowTrait;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BuildTemplate {
    private OverworldAgent plugin;
    private int id;
    private Player player;
    private String name;
    private Timestamp startTime;
    private Timestamp endTime;
    private CoreProtectAPI api;
    private NPC agent;
    private static final int MILLITOSEC = 1000;
    public BuildTemplate(OverworldAgent plugin, Player player, String name, Timestamp startTime, Timestamp endTime){
        this.player = player;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.api = this.getCoreProtect();
        this.agent = plugin.getAgents().get(player.getName());
        this.plugin = plugin;
        this.id = -1;
    }

    public void setID(int id){
        this.id = id;
    }
    public int getID(){
        return id;
    }
    public Player getPlayer(){
        return player;
    }
    public Timestamp getStartTime(){
        return startTime;
    }
    public void setEndTime(Timestamp endTime){
        this.endTime = endTime;
    }

    public Timestamp getEndTime(){
        return endTime;
    }

    public String getName(){
        return name;
    }

    public void build(){
        int step = 0;
        long startMilli = startTime.getTime();
        long current = new Timestamp(System.currentTimeMillis()).getTime();
        long endMilli = endTime.getTime();
        int time = Math.round((current-startMilli)/MILLITOSEC);
        List<String[]> lookupStart = api.performLookup(time, Arrays.asList(player.getName()), null, null, null, Arrays.asList(0,1), 0, null);
        Collections.reverse(lookupStart);
        if (agent != null && agent.isSpawned() &&  lookupStart != null) {
            Location npcStartingLocation = new Location(player.getWorld(),agent.getStoredLocation().getX(),agent.getStoredLocation().getY(),agent.getStoredLocation().getZ());
            if(agent.getOrAddTrait(FollowTrait.class).isActive()) {
                agent.getOrAddTrait(FollowTrait.class).toggle(player, false);
            }
            plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, new RebuildRunnable(plugin, player, agent, npcStartingLocation, lookupStart, step, endMilli), 0);
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
