package net.avdw.todo;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.Logger;

public class LoggingModule extends AbstractModule {
    @Override
    protected void configure() {
        Logger.getConfiguration()
                .formatPattern("{date:yyyy-MM-dd HH:mm:ss} [{thread}] {class}.{method}() {level}: {message}")
                .level(Level.TRACE).activate();

        bindInterceptor(Matchers.inSubpackage("net.avdw.todo"), Matchers.any(), (methodInvocation) -> {
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
