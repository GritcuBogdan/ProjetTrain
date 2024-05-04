package com.uca.gui;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

import java.util.Locale;

public class _FreeMarkerInitializer {

    public static Configuration getContext(Class<?> templateClass) {
        //Configure FreeMarker
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_30);
        configuration.setClassForTemplateLoading(templateClass, "/views");
        configuration.setDefaultEncoding("UTF-8");
        configuration.setLocale(Locale.FRANCE);
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        return configuration;
    }

}
