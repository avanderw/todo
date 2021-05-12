package net.avdw.todo.extension.browse;

import net.avdw.todo.domain.Todo;

import javax.inject.Inject;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Stream;

public class BrowseMapper {
    @Inject
    BrowseMapper() {
    }

    public Stream<URI> mapToUriStream(final Todo todo) {
        final List<URI> uri = new ArrayList<>();
        final Matcher matcher = BrowseStatic.PATTERN.matcher(todo.getText());
        while (matcher.find()) {
            uri.add(URI.create(matcher.group().trim()));
        }
        return uri.stream();
    }
}
