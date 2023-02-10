package edu.whimc.overworld_agent.dialoguetemplate.models;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to hold entries from config for dialogues
 */
public class DialoguePrompt {

    private String prompt;

    private String tool;

    private String feedback;

    @SuppressWarnings("unchecked")
    public DialoguePrompt(Map<?, ?> entry) {
        this.prompt = (String) entry.get("prompt");
        this.tool = (String) entry.get("tool");
        this.feedback = (String) entry.get("feedback");
    }

    public String getPrompt() {
        return this.prompt;
    }

    public String getTool() {
        return this.tool;
    }

    public String getFeedback() {
        return this.feedback;
    }

    public String toString(){
        return this.prompt;
    }

}
