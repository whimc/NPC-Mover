package edu.whimc.overworld_agent.dialoguetemplate.models;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.*;

/**
 * Class to hold entries from config for dialogues
 */
public class DialogueTag {

    private String[] aliases;

    private String feedback;

    @SuppressWarnings("unchecked")
    public DialogueTag(Map<?, ?> entry) {
        String aliases = (String) entry.get("aliases");
        aliases = aliases.toLowerCase();
        this.aliases = aliases.split(", ");
        this.feedback = (String) entry.get("feedback");
    }

    public String[] getAliases() {
        return this.aliases;
    }

    public String getFeedback() {
        return this.feedback;
    }

}