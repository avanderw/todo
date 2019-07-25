package net.avdw.todo.config;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.AbstractMatcher;
import net.avdw.todo.Ansi;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.Logger;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import static com.google.inject.matcher.Matchers.inSubpackage;
import static com.google.inject.matcher.Matchers.not;

public class LoggingModule extends AbstractModule {
    @Override
    protected void configure() {
//        String date = String.format("%s{date:HH:mm:ss}%s", Ansi.Green, Ansi.Reset);
        String level = String.format("%s{level}%s", Ansi.Blue, Ansi.Reset);
        String line = String.format("%s{line}%s", Ansi.Yellow, Ansi.Reset);
        String clazz = String.format("%s{class}%s", Ansi.White, Ansi.Reset);
        String method = String.format("%s{method}()%s", Ansi.Cyan, Ansi.Reset);
        Logger.getConfiguration()
                .formatPattern(String.format("[%s] %s:%s:%s {message}", level, clazz, line, method))
                .level(Level.INFO).activate();

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
