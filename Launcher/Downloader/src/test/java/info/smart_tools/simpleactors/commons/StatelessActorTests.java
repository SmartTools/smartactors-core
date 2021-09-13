package info.smart_tools.simpleactors.commons;

import info.smart_tools.smartactors.downloader.commons.CommonParameters;
import info.smart_tools.smartactors.downloader.commons.Executable;
import info.smart_tools.smartactors.downloader.commons.IMessage;
import info.smart_tools.smartactors.downloader.commons.Message;
import info.smart_tools.smartactors.downloader.commons.MethodParameters;
import info.smart_tools.smartactors.downloader.commons.StatelessActor;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StatelessActorTests {

    private final IMessage messageForTest1 = Message.builder().build();
    private final IMessage messageForTest2 = Message.builder().build();
    private final IMessage messageForTest3 = Message.builder().build();
    private final Object returnValue = new Object();
    private boolean checkValueTest2 = false;

    public class TestClass1 extends StatelessActor {
        @Executable
        public Object method1(
                final boolean value1,
                final double value2,
                final int value3,
                final String value4,
                final IMessage msg,
                final String value5,
                final String value6
        ){
            assertTrue(value1);
            assertEquals(1.1, value2, 0);
            assertEquals(2, value3);
            assertEquals("test1", value4);
            assertEquals(messageForTest1, msg);
            assertEquals("test2", value5);
            assertEquals("test3", value6);

            return returnValue;
        };
    }

    public class TestClass2 extends StatelessActor {
        @Executable
        public void method1(){
            checkValueTest2 = true;
        };
    }

    public class TestClass3 extends StatelessActor {
        @Executable
        public Map<String, Object> method1(){
            Map<String, Object> result = new HashMap<>();
            result.put("test1", "test1");
            result.put("test2", 10);

            return result;
        };
    }

    @Test
    public void creatingTest1() {

        String value5 = "test2";

        StatelessActor actor = new TestClass1();
        MethodParameters methodParameters = new MethodParameters(
                Arrays.asList(
                        CommonParameters.CONSTANT + CommonParameters.CONSTANT_TO_BOOLEAN + "true",
                        CommonParameters.CONSTANT + CommonParameters.CONSTANT_TO_DOUBLE + "1.1",
                        CommonParameters.CONSTANT + CommonParameters.CONSTANT_TO_INT + "2",
                        CommonParameters.CONSTANT + CommonParameters.CONSTANT_TO_STRING + "test1",
                        CommonParameters.MESSAGE,
                        "value5",
                        CommonParameters.CONSTANT + CommonParameters.CONSTANT + "test3"
                ),
                "object"
        );
        messageForTest1.put(CommonParameters.METHOD_PARAMS, methodParameters);
        messageForTest1.put("value5", value5);
        actor.execute("method1", messageForTest1);
        assertEquals(returnValue, messageForTest1.get("object"));
    }

    @Test
    public void creatingTest2() {
        StatelessActor actor = new TestClass2();
        messageForTest2.put(CommonParameters.METHOD_PARAMS, new MethodParameters());
        actor.execute("method1", messageForTest2);
        assertTrue(checkValueTest2);
    }

    @Test
    public void creatingTest3() {
        StatelessActor actor = new TestClass3();
        messageForTest3.put(
                CommonParameters.METHOD_PARAMS, new MethodParameters(null, CommonParameters.SPLIT_RESPONSE)
        );
        actor.execute("method1", messageForTest3);
        assertEquals("test1", messageForTest3.get("test1"));
        assertEquals((Integer) 10, messageForTest3.get("test2"));
    }

    @Test(expected = RuntimeException.class)
    public void exceptionOnInvoking() {
        StatelessActor actor = new TestClass3();

        actor.execute("method1", messageForTest3);
    }
}
