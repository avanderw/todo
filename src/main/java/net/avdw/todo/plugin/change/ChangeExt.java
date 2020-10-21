package net.avdw.todo.plugin.change;

import net.avdw.todo.ThrowingFunction;
import net.avdw.todo.core.Ext;
import net.avdw.todo.domain.Todo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ChangeExt implements Ext<Date>  {
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final List<String> supportedExtList = new ArrayList<>();

    ChangeExt() {
        supportedExtList.add("started");
    }

    @Override
    public List<Date> getValueList(final Todo todo) {
        return supportedExtList.stream()
                .flatMap(ext -> todo.getTagValueList(ext).stream()
                        .map(ThrowingFunction.unchecked(simpleDateFormat::parse))
                        .filter(Objects::nonNull))
                .collect(Collectors.toList());
    }
}
