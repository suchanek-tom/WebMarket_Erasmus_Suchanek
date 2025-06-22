package com.webmarket.utils;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

import java.io.IOException;

public class FreemarkerConfig {
    private static Configuration cfg;

    public static Configuration getConfig() throws IOException {
        if (cfg == null) {
            cfg = new Configuration(Configuration.VERSION_2_3_32);
            cfg.setClassLoaderForTemplateLoading(
                FreemarkerConfig.class.getClassLoader(), "templates");
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
        }
        return cfg;
    }
}
