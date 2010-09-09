package org.intalio.tempo.workflow.tms.server;

import junit.framework.TestCase;

public class LoggingInterceptorTest extends TestCase {
    // private static final Logger _logger =
    // LoggerFactory.getLogger(LoggingInterceptorTest.class);
    LoggingInterceptor l = new LoggingInterceptor();

    public static void main(String[] args) {
        junit.textui.TestRunner.run(LoggingInterceptorTest.class);
    }

    private void doSomething() {

    }

    private void throwSomeghing() throws Exception {
        throw new Exception("this is a nice test Exception");
    }

    public void testLoggingInterceptor() throws Throwable {
        l.before(LoggingInterceptorTest.class.getDeclaredMethod("doSomething"), null, this);
        doSomething();
        l.afterReturning(this, LoggingInterceptorTest.class.getDeclaredMethod("doSomething"), null, this);
        try {
            throwSomeghing();
        } catch (Exception e) {
            l.afterThrowing(LoggingInterceptorTest.class.getDeclaredMethod("testLoggingInterceptor"), null, this, e);
        }
    }
}
