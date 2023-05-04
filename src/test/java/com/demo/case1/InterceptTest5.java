package com.demo.case1;

import net.bytebuddy.agent.ByteBuddyAgent;
import org.junit.jupiter.api.Test;

import java.lang.instrument.UnmodifiableClassException;

public class InterceptTest5 extends AbstractInterceptTest {

    @Test
    public void test5() throws UnmodifiableClassException {
        ByteBuddyAgent.install();

        // install transformer
        installConstructorInterceptor(TARGET_CLASS_NAME, 1);
        installMethodInterceptor(TARGET_CLASS_NAME, SAY_HELLO_METHOD, 1);
        installConstructorInterceptor(TARGET_CLASS_NAME, 2);
        installMethodInterceptor(TARGET_CLASS_NAME, SAY_HELLO_METHOD, 2);

        // load target class
        try {
            callBizFoo(2);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            // check interceptors
            checkConstructorInterceptor(1);
            checkMethodInterceptor(SAY_HELLO_METHOD, 1);
            checkConstructorInterceptor(2);
            checkMethodInterceptor(SAY_HELLO_METHOD, 2);
        }
    }
}

