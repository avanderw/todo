package net.avdw.todo.plugin.moscow;

import com.google.inject.Inject;
import net.avdw.todo.core.selector.ExtSelector;
import net.avdw.todo.domain.Todo;

public class MoscowExtSelector extends ExtSelector {
    private static final String ext = "moscow:";
    private final MoscowMapper moscowMapper;

    @Inject
    public MoscowExtSelector(final MoscowMapper moscowMapper) {
        super(ext);
        this.moscowMapper = moscowMapper;
    }

    @Override
    public int intValue(final Todo todo) {
        return moscowMapper.mapToInt(todo);
    }
}
