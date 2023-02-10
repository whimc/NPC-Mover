package edu.whimc.overworld_agent.dialoguetemplate;

import edu.whimc.overworld_agent.OverworldAgent;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.Timestamp;

public class Interaction {
    private Player player;
    private Timestamp time;
    private OverworldAgent plugin;
    private String interaction;
    private Location location;
    public Interaction(OverworldAgent plugin, Player player, String interaction){
        this.plugin = plugin;
        this.player = player;
        this.time = new Timestamp(System.currentTimeMillis());
        this.interaction = interaction;
        location = player.getLocation();
    }

    public Location getLocation() {
        return location;
    }

    public Player getPlayer() {
        return player;
    }

    public Timestamp getTime(){
        return time;
    }

    public String getInteraction(){
        return interaction;
    }
}
