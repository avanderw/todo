package net.avdw.todo.core.style.parser;

import net.avdw.todo.Guard;

import java.util.Date;

public class AnyDateGuard implements Guard<Date> {
    @Override
    public boolean isSatisfiedBy(final Date date) {
        return true;
    }
}
