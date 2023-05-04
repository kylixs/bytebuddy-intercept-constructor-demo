package com.demo.case1;

import com.demo.*;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class AbstractInterceptTest {
    public static final String TARGET_CLASS_NAME = "com.demo.BizFoo";
    public static final String SAY_HELLO_METHOD = "sayHello";
    public static final int BASE_INT_VALUE = 100;
    public static final String CONSTRUCTOR_INTERCEPTOR_CLASS = "constructorInterceptorClass";
    public static final String METHOD_INTERCEPTOR_CLASS = "methodInterceptorClass";

    protected static void callBizFoo(int round) {
        // load target class
        int intResult = new BizFoo().sayHello(BASE_INT_VALUE);
        System.out.println(intResult);

        String result = new BizFoo("Smith").sayHello("Joe");
        System.out.println(result);

        Assertions.assertEquals(BASE_INT_VALUE + round, intResult, "Int value is unexpected");
        Assertions.assertEquals("Hello to John from Smith", result, "String value is unexpected");
    }

    protected static void checkMethodInterceptor(String method, int round) {
        List<String> interceptors = EnhanceHelper.getInterceptors();
        String interceptorName = METHOD_INTERCEPTOR_CLASS + "$" + method + "$" + round;
        Assertions.assertTrue(interceptors.contains(interceptorName), "Not found interceptor: " + interceptorName);
        System.out.println("Found interceptor: " + interceptorName);
    }

    protected static void checkConstructorInterceptor(int round) {
        List<String> interceptors = EnhanceHelper.getInterceptors();
        String interceptorName = CONSTRUCTOR_INTERCEPTOR_CLASS + "$" + round;
        Assertions.assertTrue(interceptors.contains(interceptorName), "Not found interceptor: " + interceptorName);
        System.out.println("Found interceptor: " + interceptorName);
    }

    protected void installMethodInterceptor(String className, String methodName, int round) {
        this.installMethodInterceptor(className, methodName, round, false);
    }

    protected void installMethodInterceptor(String className, String methodName, int round, boolean deleteDuplicatedFields) {
        String interceptorClassName = METHOD_INTERCEPTOR_CLASS + "$" + methodName + "$" + round;
        String fieldName = "_sw_delegate$" + methodName + round;
        new AgentBuilder.Default()
                .with(AgentBuilder.DescriptionStrategy.Default.POOL_FIRST)
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                //.with(ClassFileLocator.ForInstrumentation.fromInstalledAgent(InterceptTest1.class.getClassLoader()))
                .type(ElementMatchers.named(className))
                .transform((builder, typeDescription, classLoader, module, protectionDomain) -> {
                            if (deleteDuplicatedFields) {
                                builder = builder.visit(new MyAsmVisitorWrapper());
                            }
                            return builder
                                    .method(ElementMatchers.nameContainsIgnoreCase(methodName))
                                    .intercept(MethodDelegation.withDefaultConfiguration()
                                            .to(new InstMethodsInter(interceptorClassName, classLoader), fieldName))
                                    ;
                        }
                )
                .with(new AgentBuilder.Listener.Adapter() {
                    @Override
                    public void onError(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded, Throwable throwable) {
                        System.err.println(String.format("Transform Error: interceptorClassName: %s, typeName: %s, classLoader: %s, module: %s, loaded: %s", interceptorClassName, typeName, classLoader, module, loaded));
                        throwable.printStackTrace();
                    }
                })
                .installOn(ByteBuddyAgent.install());
    }

    protected void installConstructorInterceptor(String className, int round) {
        String interceptorClassName = CONSTRUCTOR_INTERCEPTOR_CLASS + "$" + round;
        String fieldName = "_sw_delegate$constructor" + round;
        new AgentBuilder.Default()
                .with(AgentBuilder.DescriptionStrategy.Default.POOL_FIRST)
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                //.with(ClassFileLocator.ForInstrumentation.fromInstalledAgent(InterceptTest1.class.getClassLoader()))
                .type(ElementMatchers.named(className))
                .transform((builder, typeDescription, classLoader, module, protectionDomain) -> {
                            return builder
                                    .constructor(ElementMatchers.any())
                                    .intercept(SuperMethodCall.INSTANCE.andThen(
                                            MethodDelegation.withDefaultConfiguration().to(
                                                    new ConstructorInter(interceptorClassName, classLoader), fieldName)
                                    ));
                        }
                )
                .with(new AgentBuilder.Listener.Adapter() {
                    @Override
                    public void onError(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded, Throwable throwable) {
                        System.err.println(String.format("Transform Error: interceptorClass:%s, typeName: %s, classLoader: %s, module: %s, loaded: %s", interceptorClassName, typeName, classLoader, module, loaded));
                        throwable.printStackTrace();
                    }
                })
                .installOn(ByteBuddyAgent.install());
    }
}
