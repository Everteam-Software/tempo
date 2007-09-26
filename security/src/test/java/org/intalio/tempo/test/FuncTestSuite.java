/**
 * Copyright (C) 2003, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with the
 * written permission of Intalio Inc. or in accordance with the terms
 * and conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *
 * $Id: FuncTestSuite.java,v 1.3 2005/02/24 18:20:00 boisvert Exp $
 */

package org.intalio.tempo.test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.Vector;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

/**
 * A FuncTestSuite is an extension of jUnit test suite. It supports suite 
 * level set up and tear off.
 * 
 * It is more convenient and flexible over the use of static variables 
 * with basic jUnit framework for that requires enviorment setup, like
 * functional testing.<p>
 * 
 * The method {@link #setUp} is called right before the first test case
 * in the suite is tested. The method {@link #tearOff} is called after the
 * last test in the suite.<p>
 * 
 * An optional method {@link #setSuite} of each test is called, when it is
 * added into a FuncTestSuite, or when the suite is being constructed. The 
 * method takes one parameter. The parameter type can be a variant of 
 * FuncTestSuite, meaning that it in the type of the actually FuncTestSuite
 * implementation, or the super class of the suite.<p>
 * 
 * For example, a Scheduler suite might have either the method of
 * <quote>public void setSuite( SchedulerSuite suite )</quote>
 * or
 * <quote>public void setSuite( FuncTestSuite suite )</quote><p>
 * 
 * There is a limitation. The test that contains such method must
 * be added directly, or constructed by the FuncTestSuite itself.<p>
 */
public class FuncTestSuite extends TestSuite {

    /**
     * Constructor
     */
    public FuncTestSuite() {
        super();
    }

    /**
     * Constructor
     * 
     * Create a test suite contains each test method of the class as an test
     */
    public FuncTestSuite(Class cls) {
        super(cls);
    }

    /**
     * Constructor
     * 
     * Create a test empty test suite with the specified name
     */
    public FuncTestSuite(String name) {
        super(name);
    }
    
    /**
     * Setup the suite. This method is called before the first test of
     * the suite is run.
     * 
     * @Exception
     */
    protected void setUp() throws Exception {
        // nothing
    }

    /**
     * Tear down the suite. This method is called after the last test of
     * the suite is ran.
     * 
     * @throws Exception
     */    
    protected void tearDown() throws Exception {
        // nothing
    }

    /**
     * Add a suite of the specified test class.
     * 
     * @see junit.framework.TestSuite#addTestSuite(java.lang.Class)
     */
    public final void addTestSuite( Class theClass ) {

        try {
            getTestConstructor(theClass); // Avoid generating multiple error messages
        } catch (NoSuchMethodException e) {
            addTest(warning("Class "+theClass.getName()+" has no public constructor TestCase(String name) or TestCase()"));
            return;
        }

        if (!Modifier.isPublic(theClass.getModifiers())) {
            addTest(warning("Class "+theClass.getName()+" is not public"));
            return;
        }
        
        Class superClass = theClass;
        Vector names= new Vector();
        try {
        while (Test.class.isAssignableFrom(superClass)) {
            Method[] methods= superClass.getDeclaredMethods();
            for (int i= 0; i < methods.length; i++) {
                addTestMethod(methods[i], names, theClass);
            }
            superClass= superClass.getSuperclass();
        }    
        } catch ( RuntimeException re ) {
            re.printStackTrace();
        }
    }
    
    /**
     * Add a test and call the setSuite method of the test.
     * 
     * @see junit.framework.TestSuite#addTest(junit.framework.Test)
     */
    public final void addTest( Test test ) {
        setVariantSuite( test, this );
        super.addTest(test);
    }

    /**
     * Call the setSuite method of the test. The parameter type can be a 
     * variant of FuncTestSuite, meaning that it in the type of the actually 
     * FuncTestSuite implementation, or its super class.
     * 
     * @param test the specified test in which its setSuite method is called
     * @param suite the parameter of the method
     */
    protected final void setVariantSuite( Test test, FuncTestSuite suite ) {

        try {
            Method suiteMethod = null;
            String methodName = "setSuite";
            Class[] paramTypes = new Class[1];
            Class suiteClass = suite.getClass();
            
            // obtain the setSuite method, try setSuite(suite.getClass()).
            // if not found, try the super class...
            while(suiteMethod==null) {
                try {
                    paramTypes[0] = suiteClass;
                    suiteMethod = test.getClass().getMethod(methodName, paramTypes);
                } catch (NoSuchMethodException e) {
                    suiteClass = suiteClass.getSuperclass();
                    if ( !FuncTestSuite.class.isAssignableFrom(suiteClass) ) {
                        //addTest(warning("Method \""+methodName+"\" not found"));
                        return;
                    }
                }
            }
    
            if (!Modifier.isPublic(suiteMethod.getModifiers())) {
                addTest(warning("Method \""+getName()+"\" should be public"));
                return;
            }
            
            try {
                suiteMethod.invoke(test, new Object[] {suite});
            } catch (InvocationTargetException e) {
                e.fillInStackTrace();
                addTest(error("Error seting the suite", e));
            } catch (IllegalAccessException e) {
                e.fillInStackTrace();
                addTest(error("Error setting the suite", e));
            }
        } catch ( Throwable t ) {
            t.printStackTrace();
        }

    }
    
    /**
     * Runs the tests and collects their result in a TestResult.
     */
    public void run(TestResult result) {
        // it probably make some sense to call result.run(this),
        // but, to avoid the suite being counted we try/catch ourselves
        try {
            setUp();
        } catch (AssertionFailedError e) {
            result.addFailure(this, e);
            return;
        } catch (ThreadDeath e) { // don't catch ThreadDeath by accident
            throw e;
        } catch (Throwable e) {
            result.addError(this, e);
            return;
        }

        for (Enumeration e=tests(); e.hasMoreElements(); ) {
            if (result.shouldStop() )
                break;
            Test test= (Test)e.nextElement();
            runTest(test, result);
        }
        
        try {
            tearDown();
        } catch (AssertionFailedError e) {
            result.addFailure(this, e);
            return;
        } catch (ThreadDeath e) { // don't catch ThreadDeath by accident
            throw e;
        } catch (Throwable e) {
            result.addError(this, e);
            return;
        }
    }

    /**
     * Add a test method
     * 
     * @param m
     * @param names
     * @param theClass
     */
    @SuppressWarnings("unchecked")
    private void addTestMethod(Method m, Vector names, Class theClass) {
        String name= m.getName();
        if (names.contains(name))
            return;
        if (!isTestMethod(m))
            return;
            
        names.addElement(name);
        addTest(createTest(theClass, name));
    }

    /**
     * Create a test out of the specified class with the specified method name.
     */
    static public Test createTest(Class theClass, String name) {
        Constructor constructor;
        try {
            constructor= getTestConstructor(theClass);
        } catch (NoSuchMethodException e) {
            return warning("Class "+theClass.getName()+" has no public constructor TestCase(String name) or TestCase()");
        }
        Object test;
        try {            
            if (constructor.getParameterTypes().length == 0) {
                test= constructor.newInstance(new Object[0]);
                if (test instanceof TestCase)
                    ((TestCase) test).setName(name);
            } else {
                test= constructor.newInstance(new Object[]{name});
            }
        } catch (InstantiationException e) {
            return(warning("Cannot instantiate test case: "+name+" ("+e.toString()+")"));
        } catch (InvocationTargetException e) {
            return(warning("Exception in constructor: "+name+" ("+e.toString()+")"));
        } catch (IllegalAccessException e) {
            return(warning("Cannot access test case: "+name+" ("+e.toString()+")"));
        }
        return (Test) test;
    }

    /**
     * Gets a constructor which takes a single String as
     * its argument or a no arg constructor.
     */
    public static Constructor getTestConstructor(Class theClass) throws NoSuchMethodException {
        Class[] args= { String.class };
        try {
            return theClass.getConstructor(args);   
        } catch (NoSuchMethodException e) {
            // fall through
        }
        return theClass.getConstructor(new Class[0]);
    }

    /**
     * Test if the specified method is a jUnit test method.
     * 
     * @param m the specified method to be test
     * @return true if the specified method is a jUnit method.
     */
    protected boolean isTestMethod(Method m) {
        // must be public, void, starts with "test", 
        // and take no param of FuncTestSuite type
        if (!m.getName().startsWith("test"))
            return false;

        if (!m.getReturnType().equals(Void.TYPE))
            return false;
                        
        if ( m.getParameterTypes().length!=0 )        
            return false;

        if (!Modifier.isPublic(m.getModifiers())) {
            addTest(warning("Test method isn't public: "+m.getName()));
            return false;
        }
        
        return true;
     }

    /**
     * Returns a test which will fail and _log a warning message.
     */
    /*
    private static Test warning(final String message) {
        return new TestCase("warning") {
            protected void runTest() {
                fail(message);
            }
        };
    }
    */

    /**
     * Returns a test which will fail and _log a warning message.
     */
    private static Test error(final String message, final Throwable t) {
        return new TestCase("error") {
            protected void runTest() throws Throwable {
                throw t;
            }
        };
    }

}
