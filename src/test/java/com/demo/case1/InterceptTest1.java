package com.demo.case1;

import net.bytebuddy.agent.ByteBuddyAgent;
import org.junit.Test;

import java.lang.instrument.UnmodifiableClassException;

public class InterceptTest1 extends AbstractInterceptTest {

    @Test
    public void test1() throws UnmodifiableClassException {
        ByteBuddyAgent.install();

        // install transformer
        installMethodInterceptor(TARGET_CLASS_NAME, SAY_HELLO_METHOD, 1);
        installConstructorInterceptor(TARGET_CLASS_NAME, 1);

        try {
            callBizFoo(1);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            // check interceptors
            checkMethodInterceptor(SAY_HELLO_METHOD, 1);
            checkConstructorInterceptor(1);
        }
    }
}

