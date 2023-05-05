package com.demo;

import net.bytebuddy.dynamic.ClassFileLocator;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MyClassFileLocator implements ClassFileLocator {

    private final ForInstrumentation.ClassLoadingDelegate classLoadingDelegate;
    private Instrumentation instrumentation;
    private ClassLoader classLoader;
    private String typeNameTrait;
    private ExecutorService executorService = Executors.newFixedThreadPool(1);

    public MyClassFileLocator(Instrumentation instrumentation, ClassLoader classLoader, String typeNameTrait) {
        this.instrumentation = instrumentation;
        this.classLoader = classLoader;
        this.typeNameTrait = typeNameTrait;
        classLoadingDelegate = ForInstrumentation.ClassLoadingDelegate.ForDelegatingClassLoader.of(classLoader);
    }

    @Override
    public Resolution locate(String name) throws IOException {
        // get class binary representation in a clean thread, avoiding nest calling transformer!
        Future<Resolution> future = executorService.submit(() -> getResolution(name));
        try {
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Resolution getResolution(String name) {
        ExtractionClassFileTransformer classFileTransformer = new ExtractionClassFileTransformer(classLoader, name);
        try {
            instrumentation.addTransformer(classFileTransformer, true);
            try {
                instrumentation.retransformClasses(new Class[]{classLoadingDelegate.locate(name)});
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } finally {
            instrumentation.removeTransformer(classFileTransformer);
        }

        return classFileTransformer.getBinaryRepresentation() != null ?
                new Resolution.Explicit(classFileTransformer.getBinaryRepresentation()) :
                new Resolution.Illegal(name);
    }

    @Override
    public void close() throws IOException {
    }
}
