package za.co.no9.util;

import freemarker.template.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

public final class FreeMarkerUtils {
    static private FreeMarkerUtils INSTANCE = new FreeMarkerUtils();

    private Configuration cfg = new Configuration();

    private FreeMarkerUtils() {
        cfg = new Configuration();

        cfg.setClassForTemplateLoading(FreeMarkerUtils.class, "/");
        cfg.setObjectWrapper(new DefaultObjectWrapper());
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setIncompatibleImprovements(new Version(2, 3, 20));
    }

    public static String template(Map<String, Object> dataModel, String templateName) throws IOException, TemplateException {
        Template template = INSTANCE.cfg.getTemplate(templateName);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Writer out = new OutputStreamWriter(baos);
        template.process(dataModel, out);
        out.close();
        return baos.toString();
    }
}