package org.intalio.tempo.workflow.tms.server;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.ThrowsAdvice;

public class LoggingInterceptor implements  MethodBeforeAdvice, AfterReturningAdvice, ThrowsAdvice {
    private static final Logger log = LoggerFactory.getLogger(LoggingInterceptor.class);

    public LoggingInterceptor() {
        log.info("Started logging interceptor");
    }

    public void before(Method arg0, Object[] arg1, Object arg2) throws Throwable {
        LoggerFactory.getLogger(arg2.getClass()).info("Beginning method: " + arg0.getName());
    }

    public void afterReturning(Object arg0, Method arg1, Object[] arg2, Object arg3) throws Throwable {
        LoggerFactory.getLogger(arg3.getClass()).info("Ending method: " + arg1.getName());
    }

    public void afterThrowing(Method m, Object[] args, Object target, Throwable ex) {
        LoggerFactory.getLogger(target.getClass()).info(
                "Exception in method: " + m.getName() + " Exception is: " + ex.getMessage());
    }

}