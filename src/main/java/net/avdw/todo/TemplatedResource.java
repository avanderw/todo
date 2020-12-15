package net.avdw.todo;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.gson.Gson;
import com.google.inject.Inject;
import org.tinylog.Logger;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @version 2020-10-17 Encapsulate mustache factory
 */
public class TemplatedResource {
    private final Gson gson = new Gson();
    private final ResourceBundle resourceBundle;
    private final MustacheFactory mustacheFactory;

    @Inject
    public TemplatedResource(final ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        mustacheFactory = new DefaultMustacheFactory();
    }

    public String populateKey(final String key, final String json) {
        return populate(key, resourceBundle.getString(key), gson.fromJson(json, Map.class));
    }

    private String populate(final String key, final String template, final Object object) {
        try {
            StringReader stringReader = new StringReader(template);
            Mustache mustache = mustacheFactory.compile(stringReader, key);
            StringWriter stringWriter = new StringWriter();
            return mustache.execute(stringWriter, object).toString();
        } catch (RuntimeException e) {
            Logger.debug(e);
            return "Could not populate template";
        }
    }

    public String populateKey(final String key) {
        return populateKey(key, "{}");
    }
}
