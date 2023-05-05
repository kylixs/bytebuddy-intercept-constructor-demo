package com.demo;

import net.bytebuddy.asm.Advice;

import java.lang.reflect.Method;
import java.util.Arrays;

public class InstMethodAdvice {

    @Advice.OnMethodEnter(inline = false)
    public static void onEnter(@Advice.This Object target,
                               @Advice.Origin Method method,
                               @Advice.AllArguments Object[] args) throws Exception {
        System.out.println(String.format("InstMethodAdvice.onEnter: constructor: %s, args: %s", method, Arrays.asList(args)));
    }


    @Advice.OnMethodExit(inline = false)
    public static void onExit(@Advice.This Object target,
                              @Advice.Origin Method method,
                              @Advice.AllArguments Object[] args) throws Exception {
        System.out.println(String.format("InstMethodAdvice.onExit: constructor: %s, args: %s", method, Arrays.asList(args)));
    }

}