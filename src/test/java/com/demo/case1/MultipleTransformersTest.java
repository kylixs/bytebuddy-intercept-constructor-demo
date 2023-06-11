package com.demo.case1;

import com.demo.ConstructorInter;
import com.demo.InstMethodsInter;
import com.demo.MyAsmVisitorWrapper;
import com.demo.biz.BizFoo;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;
import org.junit.Test;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;

public class MultipleTransformersTest extends AbstractRetransformTest {

    String dumpFolder = "target/class-dump";

    @Test
    public void test1() throws UnmodifiableClassException {
        String className = TARGET_CLASS_NAME;
        String nameTrait = "sw";
        boolean deleteDuplicatedFields = true;
        String methodName = SAY_HELLO_METHOD;

        // enableClassDump();

        AgentBuilder.Transformer transformer1 = new AgentBuilder.Transformer() {
            AgentBuilder.Transformer instance = this;

            @Override
            public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, ProtectionDomain protectionDomain) {
                int round = 1;
                String interceptorClassName = METHOD_INTERCEPTOR_CLASS + "$" + methodName + "$" + round;
                String fieldName = nameTrait + "_delegate$" + methodName + round;
                //SWTransformThreadLocals.setTransformer(instance);

                if (deleteDuplicatedFields) {
                    builder = builder.visit(new MyAsmVisitorWrapper());
                }
                return builder
                        .constructor(ElementMatchers.any())
                        .intercept(SuperMethodCall.INSTANCE.andThen(
                                MethodDelegation.withDefaultConfiguration().to(
                                        new ConstructorInter(CONSTRUCTOR_INTERCEPTOR_CLASS + "$" + round, classLoader), nameTrait + "_delegate$constructor" + round)
                        ))
                        .method(ElementMatchers.nameContainsIgnoreCase(methodName))
                        .intercept(MethodDelegation.withDefaultConfiguration()
                                .to(new InstMethodsInter(interceptorClassName, classLoader), fieldName));
            }
        };

        AgentBuilder.Transformer transformer2 = new AgentBuilder.Transformer() {
            AgentBuilder.Transformer instance = this;

            @Override
            public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, ProtectionDomain protectionDomain) {
                int round = 2;
                String interceptorClassName = METHOD_INTERCEPTOR_CLASS + "$" + methodName + "$" + round;
                String fieldName = nameTrait + "_delegate$" + methodName + round;
                //SWTransformThreadLocals.setTransformer(instance);

                if (deleteDuplicatedFields) {
                    builder = builder.visit(new MyAsmVisitorWrapper());
                }
                return builder
                        .constructor(ElementMatchers.any())
                        .intercept(SuperMethodCall.INSTANCE.andThen(
                                MethodDelegation.withDefaultConfiguration().to(
                                        new ConstructorInter(CONSTRUCTOR_INTERCEPTOR_CLASS + "$" + round, classLoader), nameTrait + "_delegate$constructor" + round)
                        ))
                        .method(ElementMatchers.nameContainsIgnoreCase(methodName))
                        .intercept(MethodDelegation.withDefaultConfiguration()
                                .to(new InstMethodsInter(interceptorClassName, classLoader), fieldName));
            }
        };

        Instrumentation instrumentation = ByteBuddyAgent.install();

        new AgentBuilder.Default()
//        newAgentBuilder(nameTrait)
                .type(ElementMatchers.named(className))
                .transform(transformer1)
                .type(ElementMatchers.named(className))
                .transform(transformer2)
                .with(new AgentBuilder.Listener.Adapter() {
                    @Override
                    public void onError(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded, Throwable throwable) {
                        System.err.println(String.format("Transform Error: typeName: %s, classLoader: %s, module: %s, loaded: %s", typeName, classLoader, module, loaded));
                        throwable.printStackTrace();
                    }
                })
                .installOn(instrumentation);

        try {
            callBizFoo(2);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            // check interceptors
            checkMethodInterceptor(SAY_HELLO_METHOD, 1);
            checkMethodInterceptor(SAY_HELLO_METHOD, 2);
            checkConstructorInterceptor(1);
            checkConstructorInterceptor(2);
        }

        reTransform(instrumentation, BizFoo.class);

        callBizFoo(2);

//        try {
//            TimeUnit.DAYS.sleep(1);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
    }

    private void enableClassDump() {
        System.setProperty("net.bytebuddy.dump", dumpFolder);
        File dumpDir = new File(dumpFolder);
        //  FileUtils.deleteDirectory(dumpDir);
        dumpDir.mkdirs();
    }
}
