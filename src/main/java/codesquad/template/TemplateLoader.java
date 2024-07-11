package codesquad.template;

import java.io.InputStream;

public class TemplateLoader {
    protected static final String STATIC_TEMPLATE_PATH = "/templates";

    public String loadTemplate(String templateName) {
        String resourcePath = getResourcePath(templateName);
        try (InputStream resourceAsStream = getClass().getResourceAsStream(resourcePath)) {
            if (resourceAsStream == null) {
                throw new IllegalArgumentException("Template not found: " + resourcePath);
            }
            return new String(resourceAsStream.readAllBytes());
        } catch (Exception e) {
            throw new IllegalArgumentException("Error loading template: " + resourcePath, e);
        }
    }

    public String loadTemplate(String templateName, String... args) {
        return String.format(loadTemplate(templateName), args);
    }

    private String getResourcePath(String templateName) {
        return STATIC_TEMPLATE_PATH + templateName;
    }
}
