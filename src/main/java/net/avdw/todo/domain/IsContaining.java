package net.avdw.todo.domain;

import net.avdw.todo.repository.AbstractSpecification;

import java.util.Locale;

public class IsContaining extends AbstractSpecification<Integer, Todo> {
    private final String text;
    private final boolean caseSensitive;

    public IsContaining(final String text) {
        this.text = text;
        caseSensitive = false;
    }

    public IsContaining(final String text, final boolean caseSensitive) {
        this.text = text;
        this.caseSensitive = caseSensitive;
    }

    @Override
    public boolean isSatisfiedBy(final Todo todo) {
        return caseSensitive ? todo.getText().contains(text) : todo.getText().toLowerCase(Locale.ENGLISH).contains(text.toLowerCase(Locale.ENGLISH));
    }

    @Override
    public String toString() {
        return String.format("isContaining('%s', caseSensitive='%s')", text, caseSensitive);
    }
}
