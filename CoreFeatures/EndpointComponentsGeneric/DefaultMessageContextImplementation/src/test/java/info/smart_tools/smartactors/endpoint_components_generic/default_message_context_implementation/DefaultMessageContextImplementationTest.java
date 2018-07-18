package info.smart_tools.smartactors.endpoint_components_generic.default_message_context_implementation;

import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import org.junit.Test;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

public class DefaultMessageContextImplementationTest {
    private final Object[] o = new Object[] {
            new Object(), new Object(), new Object(),
            new Object(), new Object(), new Object(),
            new Object(),
    };

    @Test
    public void Should_getCloned()
            throws Exception {
        IDefaultMessageContext context = new DefaultMessageContextImplementation();

        context.setConnectionContext(o[1]);
        context.setSrcMessage(o[2]);
        context.setDstMessage(o[3]);

        IDefaultMessageContext clone = context.clone().cast(IDefaultMessageContext.class);

        assertNotSame(context, clone);

        assertSame(context.getConnectionContext(), clone.getConnectionContext());
        assertSame(context.getSrcMessage(), clone.getSrcMessage());
        assertSame(context.getDstMessage(), clone.getDstMessage());

        clone.setConnectionContext(o[4]);
        clone.setSrcMessage(o[5]);
        clone.setDstMessage(o[6]);

        assertNotSame(context.getConnectionContext(), clone.getConnectionContext());
        assertNotSame(context.getSrcMessage(), clone.getSrcMessage());
        assertNotSame(context.getDstMessage(), clone.getDstMessage());
    }
}
