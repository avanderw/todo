package net.avdw.todo.config;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.AbstractMatcher;
import net.avdw.todo.AnsiColor;
import net.avdw.todo.item.TodoItem;
import net.avdw.todo.item.TodoItemTokenIdentifier;
import org.pmw.tinylog.Logger;

import java.lang.reflect.Method;

import static com.google.inject.matcher.Matchers.*;

public final class ProfilingModule extends AbstractModule {
    @Override
    protected void configure() {
        bindInterceptor(inSubpackage("net.avdw.todo")
                        .and(not(identicalTo(TodoItem.class)))
                        .and(not(identicalTo(TodoItemTokenIdentifier.class))),
                new AbstractMatcher<Method>() {
                    @Override
                    public boolean matches(final Method method) {
                        return !method.isSynthetic();
                    }
                }, (methodInvocation) -> {
                    long start = System.currentTimeMillis();
                    try {
                        Logger.trace(String.format("%s-> %s.%s%s",
                                AnsiColor.MAGENTA,
                                methodInvocation.getMethod().getDeclaringClass().getSimpleName(),
                                methodInvocation.getMethod().getName(),
                                AnsiColor.RESET
                        ));
                        return methodInvocation.proceed();
                    } finally {
                        Logger.trace(String.format("%s<- %s.%s : %,d ms%s",
                                AnsiColor.MAGENTA,
                                methodInvocation.getMethod().getDeclaringClass().getSimpleName(),
                                methodInvocation.getMethod().getName(),
                                System.currentTimeMillis() - start,
                                AnsiColor.RESET
                        ));
                    }
                });
    }
}
