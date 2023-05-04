package com.demo.case1;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;

public class AbstractRetransformTest extends AbstractInterceptTest {

    protected static void reTransform(Instrumentation instrumentation, Class clazz) throws UnmodifiableClassException {
        System.out.println("Begin to retransform class: " + clazz.getName() + " ..");
        ClassFileTransformer transformer = new ClassFileTransformer() {
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                    ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                System.out.println(String.format("transform: className=%s, classBeingRedefined=%s, classloader=%s, protectionDomain=%s, classfileBuffer=%d",
                        className, classBeingRedefined, loader, protectionDomain.getCodeSource(), classfileBuffer.length));
                return null;
            }
        };
        try {
            instrumentation.addTransformer(transformer, true);
            instrumentation.retransformClasses(clazz);
            System.out.println("Retransform class " + clazz.getName() + " successful.");
        } catch (Throwable e) {
            System.out.println("Retransform class " + clazz.getName() + " failure: " + e);
            throw e;
        } finally {
            instrumentation.removeTransformer(transformer);
        }
    }

}
