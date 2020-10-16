package net.avdw.todo.plugin.browse;

import com.google.inject.Inject;
import net.avdw.todo.domain.Todo;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class BrowseMapper {
    private final Pattern pattern;

    @Inject
    BrowseMapper(@Browse final Pattern pattern) {
        this.pattern = pattern;
    }

    public Stream<URI> map(final Todo todo) {
        List<URI> uri = new ArrayList<>();
        Matcher matcher = pattern.matcher(todo.getText());
        while (matcher.find()) {
            uri.add(URI.create(matcher.group().trim()));
        }
        return uri.stream();
    }
}
