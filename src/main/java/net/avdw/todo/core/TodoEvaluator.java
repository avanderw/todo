package net.avdw.todo.core;

import net.avdw.todo.core.selector.Selector;
import net.avdw.todo.domain.Todo;
import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;
import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class TodoEvaluator {
    private final String evalFunc;
    private final Expression expression;
    private final Set<Selector> selectorSet;

    public TodoEvaluator(final String evalFunc, final Set<Selector> selectorSet) {
        this.evalFunc = evalFunc;
        this.selectorSet = selectorSet;
        Logger.trace("evalFunction = '{}'", evalFunc);
        Logger.trace("selectorSet = {}", selectorSet);

        List<Argument> argumentList = new ArrayList<>();
        AtomicReference<String> func = new AtomicReference<>(evalFunc);
        selectorSet.stream()
                .filter(s -> s.isSatisfiedBy(evalFunc))
                .forEach(s -> {
                    Logger.trace("satisfied selector = {}", s);
                    func.set(func.get().replaceAll(s.replaceRegex(), s.symbol()));
                    argumentList.add(new Argument(s.symbol()));
                });
        expression = new Expression(func.get(), argumentList.toArray(new Argument[]{}));
        Logger.trace("expression = '{}'", expression.getExpressionString());
    }

    public int evaluate(final Todo todo) {
        selectorSet.stream()
                .filter(s -> s.isSatisfiedBy(evalFunc))
                .forEach(s -> expression.setArgumentValue(s.symbol(), s.mapToInt(todo)));
        return (int) expression.calculate(); // very expensive call
    }

    public boolean isValid() {
        return expression.checkSyntax();
    }
}
