package edu.whimc.overworld_agent.dialoguetemplate.events;
import edu.whimc.overworld_agent.dialoguetemplate.BuilderDialogue;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BuildAssessEvent extends Event{

        private static final HandlerList handlers = new HandlerList();
        private int id;
        private final Player user;
        private final World world;
        private final Set<UUID> teammates;

        public BuildAssessEvent(BuilderDialogue bd, Set<UUID> teammates) {
            user = bd.getPlayer();
            world = user.getWorld();
            id = bd.getId();
            this.teammates = teammates;
        }

        public int getId(){return id;}
        public String getUser(){
            return user.getName();
        }
        public String getWorld(){
            return world.getName();
        }
        public String getTeammates() {
            String result = "";
            if(teammates != null) {
                Iterator<UUID> teammateIterator = teammates.iterator();
                if (teammateIterator.hasNext()) {
                    UUID playerUUID = teammateIterator.next();
                    if(Bukkit.getPlayer(playerUUID) != null) {
                        Player player = Bukkit.getPlayer(playerUUID);
                        result += player.getName();
                    } else if(Bukkit.getOfflinePlayer(playerUUID) != null){
                        OfflinePlayer player = Bukkit.getOfflinePlayer(playerUUID);
                        result += player.getName();
                    }
                }
                while (teammateIterator.hasNext()) {
                    UUID playerUUID = teammateIterator.next();
                    if(Bukkit.getPlayer(playerUUID) != null){
                        Player player = Bukkit.getPlayer(playerUUID);
                        result += "," + player.getName();
                    } else if(Bukkit.getOfflinePlayer(playerUUID) != null){
                        OfflinePlayer player = Bukkit.getOfflinePlayer(playerUUID);
                        result += "," + player.getName();
                    }
                }
            }
            return result;
    }

        @Override
        public HandlerList getHandlers() {
            return handlers;
        }

        public static HandlerList getHandlerList() {
            return handlers;
        }
    }
