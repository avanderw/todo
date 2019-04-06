package net.avdw.todo;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.pmw.tinylog.Logger;

public class LoggingInterceptor implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Logger.trace(methodInvocation.getMethod());
        return methodInvocation.proceed();
    }
}
