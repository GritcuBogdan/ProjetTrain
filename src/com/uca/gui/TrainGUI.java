package com.uca.gui;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import com.uca.entity.Train;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

public class TrainGUI {

    public static String list(List<Train> trains) throws IOException, TemplateException {
        Configuration configuration = _FreeMarkerInitializer.getContext();

        Map<String, Object> input = new HashMap<>();
        input.put("title", "Trains");
        input.put("trains", trains);

        Writer output = new StringWriter();
        Template template = configuration.getTemplate("train.ftl");
        template.setOutputEncoding("UTF-8");
        template.process(input, output);

        return output.toString();
    }

    public static String add() throws IOException, TemplateException {
        Configuration configuration = _FreeMarkerInitializer.getContext();

        Map<String, Object> input = new HashMap<>();
        input.put("title", "Ajouter un train");

        Writer output = new StringWriter();
        Template template = configuration.getTemplate("train-add.ftl");
        template.setOutputEncoding("UTF-8");
        template.process(input, output);

        return output.toString();
    }

}
