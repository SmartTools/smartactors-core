package info.smart_tools.smartactors.core.examples;

import info.smart_tools.smartactors.core.examples.actor.FarewellMessage;
import info.smart_tools.smartactors.core.examples.actor.GreetingMessage;
import info.smart_tools.smartactors.core.examples.actor.HelloActor;
import info.smart_tools.smartactors.core.examples.actor.HelloActorException;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Example of an actor test.
 */
public class ActorExample {

    private HelloActor actor;

    @Before
    public void setUp() {
        actor = new HelloActor();
    }

    @Test
    public void testHello() throws ReadValueException, HelloActorException, ChangeValueException {
        GreetingMessage message = mock(GreetingMessage.class);
        when(message.getName()).thenReturn("Test");

        actor.hello(message);

        verify(message).setGreeting(eq("Hello, Test!"));
    }

    @Test
    public void testBye() throws ReadValueException, HelloActorException, ChangeValueException {
        FarewellMessage message = mock(FarewellMessage.class);
        when(message.getName()).thenReturn("Test");

        actor.bye(message);

        verify(message).setFarewell(eq("Bye, Test!"));
    }

    @Test(expected = HelloActorException.class)
    public void testHelloGetNameException() throws ReadValueException, HelloActorException {
        GreetingMessage message = mock(GreetingMessage.class);
        when(message.getName()).thenThrow(ReadValueException.class);

        actor.hello(message);

        fail();
    }

    @Test(expected = HelloActorException.class)
    public void testByeGetNameException() throws ReadValueException, HelloActorException {
        FarewellMessage message = mock(FarewellMessage.class);
        when(message.getName()).thenThrow(ReadValueException.class);

        actor.bye(message);

        fail();
    }

    @Test(expected = HelloActorException.class)
    public void testHelloSetGreetingException() throws ReadValueException, HelloActorException, ChangeValueException {
        GreetingMessage message = mock(GreetingMessage.class);
        when(message.getName()).thenReturn("Test");
        doThrow(ChangeValueException.class).when(message).setGreeting(any());

        actor.hello(message);

        fail();
    }

    @Test(expected = HelloActorException.class)
    public void testByeSetFarewellException() throws ReadValueException, HelloActorException, ChangeValueException {
        FarewellMessage message = mock(FarewellMessage.class);
        when(message.getName()).thenReturn("Test");
        doThrow(ChangeValueException.class).when(message).setFarewell(any());

        actor.bye(message);

        fail();
    }

}
