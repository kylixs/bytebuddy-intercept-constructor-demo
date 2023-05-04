package com.demo;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;

import java.util.Arrays;

/**
 * The actual byte-buddy's interceptor to intercept constructor methods. In this class, it provides a bridge between
 * byte-buddy and sky-walking plugin.
 */
public class ConstructorInter {
    private String interceptorClassName;
    private ClassLoader classLoader;


    /**
     * @param interceptorClassName class full name.
     */
    public ConstructorInter(String interceptorClassName, ClassLoader classLoader) {
        this.interceptorClassName = interceptorClassName;
        this.classLoader = classLoader;
    }

    /**
     * Intercept the target constructor.
     *
     * @param obj          target class instance.
     * @param allArguments all constructor arguments
     */
    @RuntimeType
    public void intercept(@This Object obj, @AllArguments Object[] allArguments) {
        EnhanceHelper.addInterceptor(interceptorClassName);
        System.out.println(String.format("ConstructorInterceptorClass: %s, target: %s, args: %s", interceptorClassName, obj, Arrays.asList(allArguments)));
    }
}