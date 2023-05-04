package com.demo.case1;

import net.bytebuddy.agent.ByteBuddyAgent;
import org.junit.jupiter.api.Test;

import java.lang.instrument.UnmodifiableClassException;

public class InterceptTest4 extends AbstractInterceptTest {

    @Test
    public void test4() throws UnmodifiableClassException {
        ByteBuddyAgent.install();

        // install transformer
        installConstructorInterceptor(TARGET_CLASS_NAME, 1);
        installMethodInterceptor(TARGET_CLASS_NAME, SAY_HELLO_METHOD, 1);

        // load target class
        try {
            callBizFoo(1);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            // check interceptors
            checkConstructorInterceptor(1);
            checkMethodInterceptor(SAY_HELLO_METHOD, 1);
        }
    }

}

