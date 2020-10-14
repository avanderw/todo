package net.avdw.todo.groupby;

import java.util.function.Function;

public interface GroupBy<T, R, S> {
    Function<T, R> collector();

    boolean isSatisfiedBy(S selector);

    String name();
}
