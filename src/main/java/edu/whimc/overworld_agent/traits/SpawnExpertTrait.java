package edu.whimc.overworld_agent.traits;

import edu.whimc.overworld_agent.Events.AgentDialogEvent;
import edu.whimc.overworld_agent.OverworldAgent;

import net.citizensnpcs.Settings;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;
import org.bukkit.Bukkit;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Class to define Novice Trait has agent able to guide player to a location on right click,
 * waits if player gets too far behind when following to its destination, and prompts player for an observation at the end of its path
 * @author sam
 */
public class SpawnExpertTrait extends Trait {
    private OverworldAgent plugin;
    @Persist private String player;

    /**
     * Constructor sets name of trait and instantiates plugin
     */
    public SpawnExpertTrait() {
        super("expertagentspawn");
        plugin = JavaPlugin.getPlugin(OverworldAgent.class);
    }

    /**
     * Work around method since traits must have empty constructors
     * @param player the player uniquely assigned to this NPC
     */
    public void setPlayer(Player player){
        this.player = player.getName();
    }



    // Here you should load up any values you have previously saved (optional).
    // This does NOT get called when applying the trait for the first time, only loading onto an existing npc at server start.
    // This is called AFTER onAttach so you can load defaults in onAttach and they will be overridden here.
    // This is called BEFORE onSpawn, npc.getEntity() will return null.
    public void load(DataKey key) {
        player = key.getString("player", player);
    }

    // Save settings for this NPC (optional). These values will be persisted to the Citizens saves file
    public void save(DataKey key) {
        key.setString("player",player);
    }

    /**
     * Event handler when the agent is right clicked on and prompts the user for an observation
     * @param event the right click event
     */
    @EventHandler
    public void click(net.citizensnpcs.api.event.NPCRightClickEvent event){
        //Handle a click on a NPC. The event has a getNPC() method.
        //Be sure to check event.getNPC() == this.getNPC() so you only handle clicks on this NPC!
        Player sender = event.getClicker();
        if(sender == Bukkit.getPlayer(player)){
            if(event.getNPC()==this.getNPC()){
                AgentDialogEvent dialog = new AgentDialogEvent(sender);
                Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getServer().getPluginManager().callEvent(dialog));
            }
        }
    }

    // Called every tick
    @Override
    public void run() {
        if(npc.isSpawned() && player != null && Bukkit.getPlayer(player) != null){
            if (!npc.getEntity().getWorld().equals(Bukkit.getPlayer(player).getWorld())) {
                if (Settings.Setting.FOLLOW_ACROSS_WORLDS.asBoolean()) {
                    npc.despawn();
                    npc.spawn(Bukkit.getPlayer(player).getLocation());
                }
                return;
            }
            npc.getNavigator().setTarget(Bukkit.getPlayer(player).getLocation());
        }
    }

    //Run code when your trait is attached to a NPC.
    //This is called BEFORE onSpawn, so npc.getEntity() will return null
    //This would be a good place to load configurable defaults for new NPCs.
    @Override
    public void onAttach() {
        plugin.getServer().getLogger().info(npc.getName() + " has been assigned ExpertTrait!");
    }

    // Run code when the NPC is despawned. This is called before the entity actually despawns so npc.getEntity() is still valid.
    @Override
    public void onDespawn() {
    }

    //Run code when the NPC is spawned. Note that npc.getEntity() will be null until this method is called.
    //This is called AFTER onAttach and AFTER Load when the server is started.
    @Override
    public void onSpawn() {

    }

    //run code when the NPC is removed. Use this to tear down any repeating tasks.
    @Override
    public void onRemove() {
    }

}
