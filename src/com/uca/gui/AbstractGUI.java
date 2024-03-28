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
 * Outils pour la génération des pages HTML basé sur le système de templates Freemarker (https://freemarker.apache.org/)
 * 
 */
public class AbstractGUI {

    /**
     * Cette méthode prend en entrée :
     * 1. le nom d'un ficher de template dans le dossier src/main/resources/views
     * 2. une association entre un nom et un object Java, chaque nom pourra être réutilisé dans le template pour accèder à la ou les valeurs que l'objet correspondant contient.
     * Ces deux entrées permettent de générer un ficher HTML sous forme d'une chaîne de caractères en sortie. 
     */
    public static String callTemplate(String file, Map<String, Object> input) throws IOException, TemplateException {
        Configuration configuration = _FreeMarkerInitializer.getContext();

        Writer output = new StringWriter();
        Template template = configuration.getTemplate(file);
        template.setOutputEncoding("UTF-8");
        template.process(input, output);

        return output.toString();
    }

}
