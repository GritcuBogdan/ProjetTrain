package com.uca.gui;

import freemarker.template.TemplateException;

import com.uca.entity.Train;

import java.io.IOException;
import java.util.*;

/**
 * Cette classe permet de générer les pages HTML qui concernent les trains
 */
public class TrainGUI {

    // retourne la page HTML affichant les trains dans une liste
    public static String list(List<Train> trains) throws IOException, TemplateException {
        Map<String, Object> input = new HashMap<>();
        input.put("title", "Trains");
        input.put("trains", trains);
        
        return AbstractGUI.callTemplate("train.ftl", input);
    }

    // retourne la page du formulaire pour ajouter un train
    public static String add() throws IOException, TemplateException {
        Map<String, Object> input = new HashMap<>();
        input.put("title", "Ajouter un train");

        return AbstractGUI.callTemplate("train-add.ftl", input);
    }

}
