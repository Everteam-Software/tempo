/**
 * Copyright (c) 2005-2008 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 */
package org.intalio.tempo.deployment.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.rmi.Remote;
import java.util.ArrayList;
import java.util.List;

/**
 * Proxy for "remote" objects, used to avoid compatibility issues with same interfaces 
 * loaded in different classloaders.
 * <p>
 * Uses reflection to identify remote interfaces (extending java.rmi.Remote) and serialization
 * to achieve pass-by-value semantic for java.io.Serializable objects
 */
public class RemoteProxy<T> implements InvocationHandler {
    private final T _remoteObject;
    private final Class<?>[] _localInterfaces;
    private final ClassLoader _localClassLoader;
    private final ClassLoader _remoteClassLoader;

    /**
     * Create a proxy for a given remote object.
     * 
     * @param remoteObject remote object 
     * @param localClassLoader local class loader where the proxy will be used
     * @param remoteClassLoader remote class loader that loaded the remote object
     */
    public RemoteProxy(T remoteObject, ClassLoader localClassLoader, ClassLoader remoteClassLoader) {
        _remoteObject = remoteObject;
        _localClassLoader = localClassLoader;
        _remoteClassLoader = remoteClassLoader;

        // resolve "local" interfaces to be implemented by proxy 
        try {
            List<Class<?>> localInterfaces  = new ArrayList<Class<?>>();
            Class<?>[] remoteInterfaces = remoteObject.getClass().getInterfaces();
            for (int i=0; i<remoteInterfaces.length; i++) {
                String className = remoteInterfaces[i].getName();
                if (Remote.class.isAssignableFrom(remoteInterfaces[i])) {
                    localInterfaces.add(Class.forName(className, true, localClassLoader));
                }
            }
            _localInterfaces  = localInterfaces.toArray(new Class<?>[localInterfaces.size()]);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public T newProxyInstance() {
        return (T) Proxy.newProxyInstance(_localClassLoader, _localInterfaces, this);
    }

    /**
     * Proxied method invocation: convert arguments to remote classes (using proxying or serialization)
     * and convert the result back into a local class. 
     */
    @SuppressWarnings("unchecked")
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<Object>[] parameterTypes = (Class<Object>[]) method.getParameterTypes();
        for (int i = 0; args != null && i < args.length; i++) {
            ConvertedObject<Object> converted = export(parameterTypes[i], args[i], _localClassLoader, _remoteClassLoader);
            args[i] = converted.convertedObject;
            parameterTypes[i] = converted.convertedType;
        }
        Method remoteMethod = _remoteObject.getClass().getMethod(method.getName(), parameterTypes);
        Object result = remoteMethod.invoke(_remoteObject, args);
        return export((Class<Object>)method.getReturnType(), result, _remoteClassLoader, _localClassLoader).convertedObject;
    }

    /**
     * Convert a type from one class loader to another, using proxying for remote objects or 
     * serialization for value-objects. 
     */
    @SuppressWarnings("unchecked")
    public static <T> ConvertedObject<T> export(Class<T> fromType, T localObject, 
                                         final ClassLoader fromClassLoader, final ClassLoader toClassLoader) 
        throws ClassNotFoundException 
    {
        T toObject;
        Class<T> toType;
        String className = fromType.getName();
        if (fromType.isPrimitive() || className.startsWith("java.")) {
            // quick passthrough for primitive/primordial objects
            toObject = localObject;
            toType = fromType;
        } else if (isDynamicProxy(localObject)) {
            RemoteProxy<T> proxy = getInvocationHandler(localObject);
            if (proxy._remoteClassLoader == toClassLoader) {
                toObject = proxy._remoteObject;
            } else {
                proxy = new RemoteProxy<T>(localObject, toClassLoader, fromClassLoader);
                toObject = proxy.newProxyInstance();
            }
            toType = (Class<T>) Class.forName(className, false, toClassLoader);
        } else if (Remote.class.isAssignableFrom(fromType)) {
            // remote objects are proxied (
            RemoteProxy<T> remote = new RemoteProxy<T>(localObject, toClassLoader, fromClassLoader);
            toObject = remote.newProxyInstance();
            toType = (Class<T>) Class.forName(className, false, toClassLoader);
        } else if (Serializable.class.isAssignableFrom(fromType)) {
            // serializable objects are passed-by-value using serialization
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(4096);
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(localObject);
                oos.close();
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray())) {
                    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
                        return Class.forName(desc.getName(), false, toClassLoader);
                    }
                };
                toObject = (T) ois.readObject();
                toType = (Class<T>) Class.forName(className, false, toClassLoader);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new IllegalArgumentException("Unsupported argument type; proxied object arguments should either be Serializable or Remote: "+fromType);
        }
        return new ConvertedObject<T>(toObject, toType);
    }        

    protected Object getProxiedObject() {
        return _remoteObject;
    }
    
    public static boolean isDynamicProxy(Object proxy) {
        return proxy != null
                && Proxy.isProxyClass(proxy.getClass())
                && Proxy.getInvocationHandler(proxy) instanceof RemoteProxy;
    }

    @SuppressWarnings("unchecked")
    protected static <T> RemoteProxy<T> getInvocationHandler(Object proxy) {
        return (RemoteProxy<T>) Proxy.getInvocationHandler(proxy);
    }
   
    static class ConvertedObject<T> {
        T convertedObject;
        Class<T> convertedType;
        
        ConvertedObject(T convertedObject, Class<T> convertedType) {
            this.convertedObject = convertedObject;
            this.convertedType = convertedType;
        }
    }
} 