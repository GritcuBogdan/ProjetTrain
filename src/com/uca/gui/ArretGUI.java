package com.uca.gui;

import com.uca.entity.Arret;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArretGUI {

    // Returns the HTML page displaying the list of stops
    public static String list(List<Arret> arrets) throws IOException, TemplateException {
        Map<String, Object> input = new HashMap<>();
        input.put("title", "Arrets");
        input.put("arrets", arrets);

        return AbstractGUI.callTemplate("arret.ftl", input);
    }

    // Returns the HTML page for adding a stop
    public static String add() throws IOException, TemplateException {
        Map<String, Object> input = new HashMap<>();
        input.put("title", "Ajouter un arret");

        return AbstractGUI.callTemplate("arret-add.ftl", input);
    }
}
