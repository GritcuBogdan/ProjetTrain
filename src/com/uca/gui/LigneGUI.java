package com.uca.gui;

import com.uca.entity.Ligne;
import freemarker.template.TemplateException;



import java.io.IOException;
import java.util.*;

public class LigneGUI {

    // Returns the HTML page displaying the list of lines
    public static String list(List<Ligne> lines) throws IOException, TemplateException {
        Map<String, Object> input = new HashMap<>();
        input.put("title", "Lignes");
        input.put("lines", lines);

        return AbstractGUI.callTemplate("ligne.ftl", input);
    }

    // Returns the HTML page for adding a line
    public static String add() throws IOException, TemplateException {
        Map<String, Object> input = new HashMap<>();
        input.put("title", "Ajouter une ligne");

        return AbstractGUI.callTemplate("ligne-add.ftl", input);
    }

}
