package net.avdw.todo;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.google.gson.Gson;
import com.google.inject.Inject;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;
import java.util.ResourceBundle;

public class TemplatedResourceBundle {
    private final Gson gson = new Gson();
    private final ResourceBundle resourceBundle;

    @Inject
    public TemplatedResourceBundle(final ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    public String getString(final String templateKey, final String json) {
        Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(resourceBundle.getString(templateKey)), templateKey);
        StringWriter stringWriter = new StringWriter();
        return mustache.execute(stringWriter, gson.fromJson(json, Map.class)).toString();
    }

    public String getString(final String templateKey) {
        return resourceBundle.getString(templateKey);
    }
}
