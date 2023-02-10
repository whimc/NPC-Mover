package edu.whimc.overworld_agent.dialoguetemplate.models;

import org.pmml4s.model.Model;

import java.io.File;


import org.bukkit.Bukkit;
import org.pmml4s.model.Model;

import java.io.File;

/**
 * Class to assess observation structure with an ML pipeline
 */
public class Chatbot {
    private String input;

    /**
     * Constructor to set instance variables
     * @param input the student's input
     */
    public Chatbot(String input){
        this.input = input;
    }

    /**
     * Runs the ML model located in the plugins directory with the uberjar, sets correctness of observation
     */
    public double[] predict(){
        String[]input = {this.input};
        Model model = null;
        try {
            model = Model.fromInputStream(this.getClass().getResourceAsStream(File.separator+"model.pmml"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Object[] result = model.predict(input);
        double[] predicted = new double[2];
        int max = 0;
        for(int k = 1; k < result.length; k++){
            if((double)result[max] < (double) result[k]){
                max = k;
            }
        }
        predicted[0] = (double) max;
        predicted[1] = (double) result[max];
        return predicted;

    }


}

