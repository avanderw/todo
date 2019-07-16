package net.avdw.todo;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.AbstractMatcher;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.Logger;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import static com.google.inject.matcher.Matchers.inSubpackage;
import static com.google.inject.matcher.Matchers.not;

public class LoggingModule extends AbstractModule {
    @Override
    protected void configure() {
        Logger.getConfiguration()
                .formatPattern("{date:yyyy-MM-dd HH:mm:ss} [{level}] {class}:{line}:{method}() {message}")
                .level(Level.TRACE).activate();

        bindInterceptor(inSubpackage("net.avdw.todo")
                        .and(not(inSubpackage("net.avdw.todo.model"))),
                new AbstractMatcher<Method>() {
                    @Override
                    public boolean matches(Method method) {
                        return !method.isSynthetic();
                    }
                },
                (methodInvocation) -> {
                    Parameter[] parameters = methodInvocation.getMethod().getParameters();
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < parameters.length; i++) {
                        stringBuilder.append(parameters[i].getName());
                        stringBuilder.append("=");
                        stringBuilder.append(methodInvocation.getArguments()[i]);
                        if (i != parameters.length - 1) {
                            stringBuilder.append(", ");
                        }
                    }
                    Logger.trace(String.format("%s.%s(%s)",
                            methodInvocation.getMethod().getDeclaringClass().getSimpleName(),
                            methodInvocation.getMethod().getName(),
                            stringBuilder.toString()
                    ));
                    return methodInvocation.proceed();
                });
    }
}
