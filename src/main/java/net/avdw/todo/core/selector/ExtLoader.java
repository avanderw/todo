package net.avdw.todo.core.selector;

import net.avdw.todo.core.TodoEvaluator;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtLoader {
    public Collection<? extends Selector> fromFunction(final String function) {
        Set<Selector> extendedSelectorSet = new HashSet<>();
        String regex = "\\S+:";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(function);
        while (matcher.find()) {
            extendedSelectorSet.add(new ExtSelector(matcher.group()));
        }

        return extendedSelectorSet;
    }
}
