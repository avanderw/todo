package net.avdw.todo.plugin.moscow;

import com.google.inject.Inject;
import net.avdw.todo.core.selector.Selector;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.Specification;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class MoscowSelector implements Selector {
    private final MoscowExt moscowExt;
    private final MoscowMapper moscowMapper;
    private final Set<Pattern> patternSet = new HashSet<>();

    @Inject
    public MoscowSelector(final MoscowMapper moscowMapper, final MoscowExt moscowExt) {
        this.moscowMapper = moscowMapper;
        this.moscowExt = moscowExt;
        moscowExt.getSupportedExtList().forEach(ext -> patternSet.add(Pattern.compile(ext)));
    }

    @Override
    public Comparator<? super Todo> comparator() {
        return Comparator.comparingInt(moscowMapper::mapToInt).reversed();
    }

    @Override
    public boolean isSatisfiedBy(final String type) {
        return patternSet.stream().anyMatch(pattern -> pattern.matcher(type).find());
    }

    @Override
    public int mapToInt(final Todo todo) {
        return moscowMapper.mapToInt(todo);
    }

    @Override
    public String replaceRegex() {
        return String.join("|", moscowExt.getSupportedExtList());
    }

    @Override
    public Specification<Integer, Todo> specification() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String symbol() {
        return "MoSCoW";
    }

    @Override
    public String toString() {
        return "MoscowSelector";
    }
}
