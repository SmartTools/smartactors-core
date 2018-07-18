package info.smart_tools.smartactors.endpoint_components_generic.dead_end_message_handler;

import org.junit.Test;

public class DeadEndMessageHandlerTest {

    @Test public void Should_doNothing() throws Exception {
        new DeadEndMessageHandler<>().handle(null, null);
    }
}
