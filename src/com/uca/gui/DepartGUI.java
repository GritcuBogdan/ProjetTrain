package com.uca.gui;

import com.uca.entity.Depart;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DepartGUI {

    // Method to render the HTML page displaying departures for a specific train
    public static String list(List<Depart> departures, int trainNumber) throws IOException, TemplateException {
        Map<String, Object> input = new HashMap<>();
        input.put("title", "Départs du train " + trainNumber);
        input.put("departures", departures);
        input.put("trainNumber", trainNumber);
        return AbstractGUI.callTemplate("depart.ftl", input);
    }

    // Method to render the HTML form for adding a departure for a specific train
    public static String addForm(Map<String, Object> input, int noligne, String title) throws IOException, TemplateException {
        input.put("noligne", noligne);
        input.put("title", title);
        return AbstractGUI.callTemplate("depart-add.ftl", input);
    }


}
