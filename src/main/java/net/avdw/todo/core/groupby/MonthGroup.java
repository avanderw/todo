package net.avdw.todo.core.groupby;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.avdw.todo.ResourceBundleKey;
import net.avdw.todo.TemplatedResource;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.extension.change.ChangeMapper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.function.Function;

@Singleton
public class MonthGroup implements Group<Todo, String> {
    private final ChangeMapper changeMapper;
    private final SimpleDateFormat collectMonthlyFormat = new SimpleDateFormat("MMMMM yyyy");
    private final TemplatedResource templatedResource;

    @Inject
    public MonthGroup(final TemplatedResource templatedResource, final ChangeMapper changeMapper) {
        this.templatedResource = templatedResource;
        this.changeMapper = changeMapper;
    }

    @Override
    public Function<Todo, String> collector() {
        return todo -> {
            Date date = changeMapper.mapToChange(todo).getDate();
            return date == null
                    ? templatedResource.populateKey(ResourceBundleKey.UNKNOWN_DATE)
                    : collectMonthlyFormat.format(date);
        };
    }

    @Override
    public boolean isSatisfiedBy(final String selector) {
        return selector.toLowerCase(Locale.ENGLISH).equals("month");
    }

    @Override
    public String name() {
        return "";
    }
}
