package com.demo;

import net.bytebuddy.asm.Advice;

import java.util.Arrays;

public class ConstructorAdvice {

    @Advice.OnMethodExit
    public static void onExit(@Advice.Origin String method,
                              @Advice.AllArguments Object[] args) throws Exception {
        System.out.println(String.format("ConstructorAdvice: method: %s, args: %s", method, Arrays.asList(args)));
    }
}