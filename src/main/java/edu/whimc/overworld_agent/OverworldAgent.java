package edu.whimc.overworld_agent;
import edu.whimc.overworld_agent.commands.DespawnAgentsCommand;
import edu.whimc.overworld_agent.commands.ExpertSpawnCommand;
import edu.whimc.overworld_agent.commands.NoviceSpawnCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;
import edu.whimc.overworld_agent.traits.*;
import net.citizensnpcs.api.npc.NPC;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.event.Listener;

//This is your bukkit plugin class. Use it to hook your trait into Citizens and handle any commands.

/**
 * Class to create plugin and enable it in MC
 * @author sam
 */
public class OverworldAgent extends JavaPlugin implements Listener {
    private static OverworldAgent instance;
    private Map<String, NPC> agents;
    public static final String PERM_PREFIX = "whimc-agent";
    /**
     * Method to return instance of plugin (helps to grab config for skins)
     * @return instance of OverworldAgent plugin
     */
    public static OverworldAgent getInstance() {
        return instance;
    }

    /**
     * Method to enable plugin
     */
    @Override
    public void onEnable() {
        saveDefaultConfig();
        OverworldAgent.instance = this;
        //check if Citizens is present and enabled.
        agents = new HashMap<>();
        if(getServer().getPluginManager().getPlugin("Citizens") == null || getServer().getPluginManager().getPlugin("Citizens").isEnabled() == false) {
            getLogger().log(Level.SEVERE, "Citizens 2.0 not found or not enabled");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        Permission parent = new Permission(PERM_PREFIX + ".*");
        Bukkit.getPluginManager().addPermission(parent);

        //Register your traits with Citizens.
        net.citizensnpcs.api.CitizensAPI.getTraitFactory().registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(SpawnNoviceTrait.class).withName("noviceagentspawn"));
        net.citizensnpcs.api.CitizensAPI.getTraitFactory().registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(SpawnExpertTrait.class).withName("expertagentspawn"));
        NoviceSpawnCommand noviceSpawnCommand = new NoviceSpawnCommand(this);
        getCommand("novicespawn").setExecutor(noviceSpawnCommand);
        getCommand("novicespawn").setTabCompleter(noviceSpawnCommand);

        ExpertSpawnCommand expertSpawnCommand = new ExpertSpawnCommand(this);
        getCommand("expertspawn").setExecutor(expertSpawnCommand);
        getCommand("expertspawn").setTabCompleter(expertSpawnCommand);

        DespawnAgentsCommand despawnCommand = new DespawnAgentsCommand(this);
        getCommand("despawnagents").setExecutor(despawnCommand);
        getCommand("despawnagents").setTabCompleter(despawnCommand);

        Bukkit.getServer().getPluginManager().registerEvents(this, this);
    }

    /**
     * When players leave their agent is despawned
     * @param event PlayerQuitEvent
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        NPC npc = agents.get(player.getName());
        if(npc != null) {
            npc.despawn();
        }
    }

    /**
     * When players join their agent is spawned
     * @param event PlayerJoinEvent
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        NPC npc = agents.get(player.getName());
        if(npc != null) {
            npc.spawn(player.getLocation());
        }
    }

    public Map<String, NPC> getAgents(){return agents;}

    public void removeAgents(){
        agents = new HashMap<>();
    }

}
