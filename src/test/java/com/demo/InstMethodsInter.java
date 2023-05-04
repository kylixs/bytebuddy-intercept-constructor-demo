package com.demo;

import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Callable;

/**
 * @author gongdewei 2023/4/16
 */
public class InstMethodsInter {
    private String interceptorClassName;
    private ClassLoader classLoader;

    public InstMethodsInter(String interceptorClassName, ClassLoader classLoader) {
        this.interceptorClassName = interceptorClassName;
        this.classLoader = classLoader;
    }

    @RuntimeType
    public Object intercept(@This Object obj, @AllArguments Object[] allArguments, @SuperCall Callable<?> zuper,
                            @Origin Method method) throws Throwable {
        EnhanceHelper.addInterceptor(interceptorClassName);

        Object originResult = zuper.call();
        Object finalResult;
        if (originResult instanceof String) {
            String result = (String) originResult;
            result = result.replaceAll("Joe", "John");
            finalResult = result;
        } else if (originResult instanceof Integer) {
            Integer result = (Integer) originResult;
            finalResult = result + 1;
        } else {
            finalResult = originResult;
        }

        System.out.printf("InstMethodInterceptorClass: %s, target: %s, args: %s, SuperCall: %s, method: %s, originResult: %s, finalResult: %s\n",
                interceptorClassName, obj, Arrays.asList(allArguments), zuper, method, originResult, finalResult);
        return finalResult;
    }


}
