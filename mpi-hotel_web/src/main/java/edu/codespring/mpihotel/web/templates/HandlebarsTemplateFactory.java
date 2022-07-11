package edu.codespring.mpihotel.web.templates;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HandlebarsTemplateFactory {
    private static final Logger LOG = LoggerFactory.getLogger(HandlebarsTemplateFactory.class);

    private static Handlebars handlebars;

    public static synchronized Template getTemplate(String templateName) throws IOException {
        if (handlebars == null) {
            LOG.info("Building handlebars renderer");

            TemplateLoader loader = new ClassPathTemplateLoader();
            loader.setPrefix("/templates");
            loader.setSuffix(".hbs");
            handlebars = new Handlebars(loader);
        }

        return handlebars.compile(templateName);
    }
}
