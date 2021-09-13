package info.smart_tools.simpleactors.commons;

import info.smart_tools.smartactors.downloader.commons.CommonParameters;
import info.smart_tools.smartactors.downloader.commons.Executable;
import info.smart_tools.smartactors.downloader.commons.IActor;
import info.smart_tools.smartactors.downloader.commons.IMessage;
import info.smart_tools.smartactors.downloader.commons.Message;
import info.smart_tools.smartactors.downloader.commons.MethodParameters;
import info.smart_tools.smartactors.downloader.commons.StatefulActor;
import org.junit.Test;

public class StatefulActorTests {

    private final IMessage message1 = Message.builder().build();
    private final IMessage message2 = Message.builder().build();

    public class TestClass extends StatefulActor {

        @Executable()
        public void method1() {
            try {

            } catch (Exception e) {

            }
        }

        @Executable()
        public void method2() {
            try {
            } catch (Exception e) {

            }
        }
    }

    @Test
    //ToDo: need to add test logic
    public void checkThreadSafe() throws Exception {
        IActor actor = new TestClass();
        Thread thread1 = new Thread(
                () -> {
                    message1.put(
                            CommonParameters.METHOD_PARAMS, new MethodParameters(null, null)
                    );
                    actor.execute("method1", message1);
                }
        );
        Thread thread2 = new Thread(
                () -> {
                    message2.put(
                            CommonParameters.METHOD_PARAMS, new MethodParameters(null, null)
                    );
                    actor.execute("method2", message2);
                }
        );
        thread1.start();
        thread2.start();
    }
}
