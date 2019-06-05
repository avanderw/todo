package net.avdw.todo;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matchers;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.Logger;

import java.lang.reflect.Method;

import static com.google.inject.matcher.Matchers.inSubpackage;
import static com.google.inject.matcher.Matchers.not;

public class LoggingModule extends AbstractModule {
    @Override
    protected void configure() {
        Logger.getConfiguration()
                .formatPattern("{date:yyyy-MM-dd HH:mm:ss} [{thread}] {class}.{method}() {level}: {message}")
                .level(Level.TRACE).activate();

        bindInterceptor(inSubpackage("net.avdw.todo").and(not(inSubpackage("net.avdw.todo.repository.model"))),
                new AbstractMatcher<Method>() {
                    @Override
                    public boolean matches(Method method) {
                        return !method.isSynthetic();
                    }
                },
                (methodInvocation) -> {
                    long start = System.currentTimeMillis();
                    try {
                        Logger.trace(String.format("-> %s.%s()", methodInvocation.getMethod().getDeclaringClass().getSimpleName(), methodInvocation.getMethod().getName()));
                        return methodInvocation.proceed();
                    } finally {
                        Logger.trace(String.format("<- %s.%s( %,d ms )",
                                methodInvocation.getMethod().getDeclaringClass().getSimpleName(),
                                methodInvocation.getMethod().getName(),
                                System.currentTimeMillis() - start));
                    }
                });
    }
}
