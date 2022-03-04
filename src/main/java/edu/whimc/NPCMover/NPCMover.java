package edu.whimc.NPCMover;
import edu.whimc.NPCMover.commands.ExpertSpawnCommand;
import edu.whimc.NPCMover.commands.NoviceSpawnCommand;
import org.bukkit.plugin.java.JavaPlugin;
import edu.whimc.NPCMover.traits.*;
import java.util.logging.Level;

//This is your bukkit plugin class. Use it to hook your trait into Citizens and handle any commands.

/**
 * Class to create plugin and enable it in MC
 * @author sam
 */
public class NPCMover extends JavaPlugin {
    private static NPCMover instance;

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

        if(getServer().getPluginManager().getPlugin("Citizens") == null || getServer().getPluginManager().getPlugin("Citizens").isEnabled() == false) {
            getLogger().log(Level.SEVERE, "Citizens 2.0 not found or not enabled");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        //Register your traits with Citizens.
        net.citizensnpcs.api.CitizensAPI.getTraitFactory().registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(SpawnNoviceTrait.class).withName("noviceagentspawn"));
        net.citizensnpcs.api.CitizensAPI.getTraitFactory().registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(SpawnExpertTrait.class).withName("expertagentspawn"));
        NoviceSpawnCommand noviceSpawnCommand = new NoviceSpawnCommand();
        getCommand("novicespawn").setExecutor(noviceSpawnCommand);
        getCommand("novicespawn").setTabCompleter(noviceSpawnCommand );

        ExpertSpawnCommand expertSpawnCommand = new ExpertSpawnCommand();
        getCommand("expertspawn").setExecutor(expertSpawnCommand);
        getCommand("expertspawn").setTabCompleter(expertSpawnCommand);

    }

}
