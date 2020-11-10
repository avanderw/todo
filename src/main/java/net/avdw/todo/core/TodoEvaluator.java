package net.avdw.todo.core;

import net.avdw.todo.core.selector.Selector;
import net.avdw.todo.domain.Todo;
import org.mariuszgromada.math.mxparser.Constant;
import org.mariuszgromada.math.mxparser.Expression;
import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class TodoEvaluator {
    private final String evalFunc;
    private final String expression;
    private final Set<Selector> selectorSet;

    public TodoEvaluator(final String evalFunc, final Set<Selector> selectorSet) {
        this.evalFunc = evalFunc;
        this.selectorSet = selectorSet;
        Logger.debug("evalFunction = {}", evalFunc);
        Logger.debug("selectorSet = {}", selectorSet);
        AtomicReference<String> func = new AtomicReference<>(evalFunc);
        selectorSet.stream()
                .filter(s -> s.isSatisfiedBy(evalFunc))
                .forEach(s -> {
                    Logger.debug("satisfied selector = {}", s);
                    func.set(func.get().replaceAll(s.regex(), s.symbol()));
                });
        expression = func.get();
        Logger.debug("expression = {}", expression);
    }

    public int evaluate(final Todo todo) {
        List<Constant> constantList = new ArrayList<>();
        selectorSet.stream()
                .filter(s -> s.isSatisfiedBy(evalFunc))
                .forEach(s -> constantList.add(new Constant(String.format("%s = %s", s.symbol(), s.intValue(todo)))));
        Expression e = new Expression(expression, constantList.toArray(new Constant[]{}));
        return (int) e.calculate();
    }
}
