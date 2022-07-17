package edu.whimc.overworld_agent.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * A custom event to be fired whenever a player creates an observation.
 */
public class AgentDialogEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;

    public AgentDialogEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}