package net.avdw.todo.plugin.browse;

import net.avdw.todo.domain.Todo;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class BrowseMapper {
    public Stream<URI> mapToUriStream(final Todo todo) {
        List<URI> uri = new ArrayList<>();
        Matcher matcher = BrowseStatic.PATTERN.matcher(todo.getText());
        while (matcher.find()) {
            uri.add(URI.create(matcher.group().trim()));
        }
        return uri.stream();
    }
}
