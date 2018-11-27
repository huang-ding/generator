package org.base.hd.generator.util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author huangding
 * @description
 * @date 2018/11/27 14:30
 */
@Component
public class FreemarkerUtil {

    @Autowired
    private Configuration configuration;

    public String processString(String templateName, Map<String, Object> params)
        throws IOException, TemplateException {

        Template template = configuration.getTemplate(templateName);
        String htmlText = processTemplateIntoString(template, params);
        return htmlText;
    }

    public String processTemplateIntoString(Template template, Object model)
        throws IOException, TemplateException {

        StringWriter result = new StringWriter();
        template.process(model, result);
        return result.toString();
    }
}
