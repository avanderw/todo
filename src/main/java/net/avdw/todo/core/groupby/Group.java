package net.avdw.todo.core.groupby;

import net.avdw.todo.core.Guard;

import java.util.function.Function;

/**
 *
 * @param <T> value to group into a list
 * @param <R> key / bucket to the list
 */
public interface Group<T, R> extends Guard<String> {
    Function<T, R> collector();

    String name();
}
