package net.avdw.todo.style.parser;

import java.util.Optional;

public interface IParser<T> {
    Optional<T> parse(String key);
}
