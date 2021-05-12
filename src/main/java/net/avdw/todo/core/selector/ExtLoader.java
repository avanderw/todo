package net.avdw.todo.core.selector;

import javax.inject.Inject;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtLoader {
    @Inject
    ExtLoader() {
    }

    public Collection<? extends Selector> fromFunction(final String function) {
        final Set<Selector> extendedSelectorSet = new HashSet<>();
        final String regex = "\\S+:";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(function);
        while (matcher.find()) {
            extendedSelectorSet.add(new ExtSelector(matcher.group()));
        }

        return extendedSelectorSet;
    }
}
