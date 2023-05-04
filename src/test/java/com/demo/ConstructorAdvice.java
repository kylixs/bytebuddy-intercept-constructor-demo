package com.demo;

import net.bytebuddy.asm.Advice;

import java.lang.reflect.Constructor;
import java.util.Arrays;

public class ConstructorAdvice {

    @Advice.OnMethodExit(inline = false)
    public static void onExit(@Advice.Origin Constructor constructor,
                              @Advice.AllArguments Object[] args) throws Exception {
        System.out.println(String.format("ConstructorAdvice: constructor: %s, args: %s", constructor, Arrays.asList(args)));
    }
}