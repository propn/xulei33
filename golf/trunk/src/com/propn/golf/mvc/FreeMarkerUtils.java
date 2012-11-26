/**
 * 
 */
package com.propn.golf.mvc;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

/**
 * @author Thunder.Hsu
 * 
 */
public class FreeMarkerUtils {

    private static Configuration cfg;

    static {
        init();
    }

    public static void init() {
        cfg = new Configuration();
        cfg.setServletContextForTemplateLoading(GolfFilter.getAppPath(), "WEB-INF/templates");
        cfg.setTemplateUpdateDelay(0);
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
        cfg.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setOutputEncoding("UTF-8");
        cfg.setLocale(Locale.CHINA);
    }

    public static String build(String tpl, Map model) throws TemplateException, IOException {
        Template requestTemplate = cfg.getTemplate(tpl);
        StringWriter out = new StringWriter();
        requestTemplate.process(model, out);
        String reqString = out.toString();
        return reqString;
    }

}
