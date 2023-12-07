package edu.whimc.overworld_agent.dialoguetemplate.events;
import edu.whimc.overworld_agent.dialoguetemplate.BuilderDialogue;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class BuildAssessEvent extends Event{

        private static final HandlerList handlers = new HandlerList();
        private int id;
        private final Player user;
        private final World world;
        private final Set<String> teammates;

        public BuildAssessEvent(BuilderDialogue bd, Set<String> teammates) {
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
                Iterator<String> teammateIterator = teammates.iterator();
                if (teammateIterator.hasNext()) {
                    result += teammateIterator.next();
                }
                while (teammateIterator.hasNext()) {
                    result += " " + teammateIterator.next();
                }
            }
            //return result;
            user.sendMessage("Teammates: " + result);
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
