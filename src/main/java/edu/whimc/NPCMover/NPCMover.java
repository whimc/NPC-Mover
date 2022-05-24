package edu.whimc.NPCMover;
import edu.whimc.NPCMover.commands.DespawnAgentsCommand;
import edu.whimc.NPCMover.commands.ExpertSpawnCommand;
import edu.whimc.NPCMover.commands.NoviceSpawnCommand;
import org.bukkit.plugin.java.JavaPlugin;
import edu.whimc.NPCMover.traits.*;
import net.citizensnpcs.api.npc.NPC;
import java.util.ArrayList;
import java.util.logging.Level;

//This is your bukkit plugin class. Use it to hook your trait into Citizens and handle any commands.

/**
 * Class to create plugin and enable it in MC
 * @author sam
 */
public class NPCMover extends JavaPlugin {
    private static NPCMover instance;
    private ArrayList<NPC> agents;
    /**
     * Method to return instance of plugin (helps to grab config for skins)
     * @return instance of NPCMover plugin
     */
    public static NPCMover getInstance() {
        return instance;
    }

    /**
     * Method to enable plugin
     */
    @Override
    public void onEnable() {
        NPCMover.instance = this;
        //check if Citizens is present and enabled.
        agents = new ArrayList<>();
        if(getServer().getPluginManager().getPlugin("Citizens") == null || getServer().getPluginManager().getPlugin("Citizens").isEnabled() == false) {
            getLogger().log(Level.SEVERE, "Citizens 2.0 not found or not enabled");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

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


    }

    public ArrayList<NPC> getAgents(){return agents;}

    public void removeAgents(){
        agents = new ArrayList<>();
    }

}
