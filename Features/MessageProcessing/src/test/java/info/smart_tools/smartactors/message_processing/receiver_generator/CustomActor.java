package info.smart_tools.smartactors.message_processing.receiver_generator;

public class CustomActor {

    void doSomeWork(ICustomWrapper wrapper)
            throws Exception {
        try {
            wrapper.getIntValue();
            wrapper.setIntValue(2);
        } catch (Throwable e) {
            throw new Exception("");
        }
    }
}
