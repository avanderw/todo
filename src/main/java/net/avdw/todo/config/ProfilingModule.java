package net.avdw.todo.config;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.AbstractMatcher;
import net.avdw.todo.Ansi;
import org.pmw.tinylog.Logger;

import java.lang.reflect.Method;

import static com.google.inject.matcher.Matchers.inSubpackage;
import static com.google.inject.matcher.Matchers.not;

public class ProfilingModule extends AbstractModule {
    @Override
    protected void configure() {
        bindInterceptor(inSubpackage("net.avdw.todo")
                        .and(not(inSubpackage("net.avdw.todo.model"))),
                new AbstractMatcher<Method>() {
                    @Override
                    public boolean matches(Method method) {
                        return !method.isSynthetic();
                    }
                }, (methodInvocation) -> {
                    long start = System.currentTimeMillis();
                    try {
                        Logger.trace(String.format("%s-> %s.%s%s",
                                Ansi.Magenta,
                                methodInvocation.getMethod().getDeclaringClass().getSimpleName(),
                                methodInvocation.getMethod().getName(),
                                Ansi.Reset
                        ));
                        return methodInvocation.proceed();
                    } finally {
                        Logger.trace(String.format("%s<- %s.%s : %,d ms%s",
                                Ansi.Magenta,
                                methodInvocation.getMethod().getDeclaringClass().getSimpleName(),
                                methodInvocation.getMethod().getName(),
                                System.currentTimeMillis() - start,
                                Ansi.Reset
                        ));
                    }
                });
    }
}