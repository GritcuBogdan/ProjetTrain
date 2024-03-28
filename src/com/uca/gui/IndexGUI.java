package com.uca.gui;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * Générateur pour la page d'accueil (voir TrainGUI pour plus de détails)
 */
public class IndexGUI {

    public static String getIndex() throws IOException, TemplateException {
        Map<String, Object> input = new HashMap<>();
        input.put("title", "Accueil");

        return AbstractGUI.callTemplate("index.ftl", input);
    }
}
