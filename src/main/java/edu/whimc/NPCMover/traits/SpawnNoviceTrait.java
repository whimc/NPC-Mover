package edu.whimc.NPCMover.traits;

import edu.whimc.NPCMover.NPCMover;
import net.citizensnpcs.api.ai.event.NavigationCompleteEvent;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;

import org.bukkit.Bukkit;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;



//This is your trait that will be applied to a npc using the /trait mytraitname command. Each NPC gets its own instance of this class.
//the Trait class has a reference to the attached NPC class through the protected field 'npc' or getNPC().
//The Trait class also implements Listener so you can add EventHandlers directly to your trait.
/**
 * Class to define Novice Trait has agent able to guide player to a location on right click,
 * waits if player gets too far behind when following to its destination, and prompts player for an observation at the end of its path
 * @author sam
 */
public class SpawnNoviceTrait extends Trait {
    private NPCMover plugin;
    boolean SomeSetting = false;
    // see the 'Persistence API' section
    @Persist("player") String player;
    boolean seenMessage = false;

    /**
     * Constructor sets name of trait and instantiates plugin
     */
    public SpawnNoviceTrait() {
        super("noviceagentspawn");
        plugin = JavaPlugin.getPlugin(NPCMover.class);
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
     * Event handler when the agent is right clicked on and starts the path to the destination
     * @param event the right click event
     */
    @EventHandler
    public void click(net.citizensnpcs.api.event.NPCRightClickEvent event){
        //Handle a click on a NPC. The event has a getNPC() method.
        //Be sure to check event.getNPC() == this.getNPC() so you only handle clicks on this NPC!
        Player sender = event.getClicker();
        if(sender == Bukkit.getPlayer(player)) {
            if (event.getNPC() == this.getNPC()) {

                NPC npc = event.getNPC();
                npc.getNavigator().setTarget(npc.getStoredLocation().add(10, 50, 0));
            }
        }
    }

    /**
     * Event handler when the agent completes path it prompts the user to observe their surroundings
     * @param event event to signify the agent's path is complete
     */
    @EventHandler
    public void endPath(NavigationCompleteEvent event){
        if(event.getNPC()==this.getNPC()) {
            Bukkit.dispatchCommand(Bukkit.getPlayer(player), "observe");
        }
    }


    /**
     * Run method to check at every tick of the server if the player is within a radius of the agent
     * if not stops and sends a message and waits until the player is back in range
     */
    @Override
    public void run() {
        if(this.getNPC().getEntity() != null) {

            if (Bukkit.getPlayer(player).getLocation().distance(this.getNPC().getEntity().getLocation()) > 5) {
                npc.getNavigator().getLocalParameters().speedModifier(0);
                if(seenMessage == false){
                    Bukkit.getPlayer(player).sendMessage("Try not to get lost! I'll wait here for you.");
                    seenMessage = true;
                }
            } else {
                npc.getNavigator().getLocalParameters().speedModifier(npc.getNavigator().getDefaultParameters().speed());
                seenMessage = false;
            }
        }
    }

    //Run code when your trait is attached to a NPC.
    //This is called BEFORE onSpawn, so npc.getEntity() will return null
    //This would be a good place to load configurable defaults for new NPCs.
    @Override
    public void onAttach() {
        plugin.getServer().getLogger().info(npc.getName() + " has been assigned MyTrait!");
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



