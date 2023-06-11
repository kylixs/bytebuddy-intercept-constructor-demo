package com.demo.case1;

import net.bytebuddy.agent.ByteBuddyAgent;
import org.junit.Test;

import java.lang.instrument.UnmodifiableClassException;

public class InterceptTest2 extends AbstractInterceptTest {

    @Test
    public void test2() throws UnmodifiableClassException {
        ByteBuddyAgent.install();

        // install transformer
        installMethodInterceptor(TARGET_CLASS_NAME, SAY_HELLO_METHOD, 1);
        installMethodInterceptor(TARGET_CLASS_NAME, SAY_HELLO_METHOD, 2);
        installConstructorInterceptor(TARGET_CLASS_NAME, 1);

        // load target class
        try {
            callBizFoo(2);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            // check interceptors
            checkMethodInterceptor(SAY_HELLO_METHOD, 1);
            checkMethodInterceptor(SAY_HELLO_METHOD, 2);
            checkConstructorInterceptor(1);
        }
    }
}

