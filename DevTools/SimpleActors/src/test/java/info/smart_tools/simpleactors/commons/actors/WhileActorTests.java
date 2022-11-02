package info.smart_tools.simpleactors.commons.actors;

import info.smart_tools.simpleactors.commons.CommonParameters;
import info.smart_tools.simpleactors.commons.IActor;
import info.smart_tools.simpleactors.commons.IRoutingSlip;
import info.smart_tools.simpleactors.commons.IStep;
import info.smart_tools.simpleactors.commons.Message;
import info.smart_tools.simpleactors.commons.MethodParameters;
import info.smart_tools.simpleactors.commons.RoutingSlip;
import info.smart_tools.simpleactors.commons.Step;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WhileActorTests {

    @Test
    public void whileReturnToStep__checkTrueCase() {
        MethodParameters methodParameters2 = new MethodParameters(
                Arrays.asList("condition", "pointToReturn", CommonParameters.SLIP), null
        );
        IStep step1 = new Step(
                "test1",
                "test1",
                new MethodParameters(null, null)
        );
        IStep step2 = new Step(
                "WhileActor",
                "whileReturnToStep",
                methodParameters2
        );
        IRoutingSlip routingSlip = new RoutingSlip("test", step1, step2);
        assertTrue(routingSlip.hasNext());
        IStep currentStep = routingSlip.next();
        assertEquals(step1, currentStep);
        currentStep = routingSlip.next();
        assertEquals(step2, currentStep);
        IActor whileActor = new WhileActor();
        Message message = Message
                .builder()
                .add(CommonParameters.METHOD_PARAMS, methodParameters2)
                .add(CommonParameters.SLIP, routingSlip)
                .add("condition", ((ConditionalInterface)()-> true))
                .add("pointToReturn", 0)
                .build();
        whileActor.execute("whileReturnToStep", message);
        currentStep = routingSlip.next();
        assertEquals(step1, currentStep);
    }

    @Test
    public void whileReturnToStep__checkFalseCase() {
        MethodParameters methodParameters2 = new MethodParameters(
                Arrays.asList("condition", "pointToReturn", CommonParameters.SLIP), null
        );
        IStep step1 = new Step(
                "test1",
                "test1",
                new MethodParameters(null, null)
        );
        IStep step2 = new Step(
                "WhileActor",
                "whileReturnToStep",
                methodParameters2
        );
        IRoutingSlip routingSlip = new RoutingSlip("test", step1, step2);
        assertTrue(routingSlip.hasNext());
        IStep currentStep = routingSlip.next();
        assertEquals(step1, currentStep);
        currentStep = routingSlip.next();
        assertEquals(step2, currentStep);
        IActor whileActor = new WhileActor();
        Message message = Message
                .builder()
                .add(CommonParameters.METHOD_PARAMS, methodParameters2)
                .add(CommonParameters.SLIP, routingSlip)
                .add("condition", ((ConditionalInterface)()-> false))
                .add("pointToReturn", 0)
                .build();
        whileActor.execute("whileReturnToStep", message);
        assertFalse(routingSlip.hasNext());
    }

    @Test
    public void whileReturnByShift__checkTrueCase() {
        MethodParameters methodParameters2 = new MethodParameters(
                Arrays.asList("condition", "shift", CommonParameters.SLIP), null
        );
        IStep step1 = new Step(
                "test1",
                "test1",
                new MethodParameters(null, null)
        );
        IStep step2 = new Step(
                "WhileActor",
                "whileReturnByShift",
                methodParameters2
        );
        IRoutingSlip routingSlip = new RoutingSlip("test", step1, step2);
        assertTrue(routingSlip.hasNext());
        IStep currentStep = routingSlip.next();
        assertEquals(step1, currentStep);
        currentStep = routingSlip.next();
        assertEquals(step2, currentStep);
        IActor whileActor = new WhileActor();
        Message message = Message
                .builder()
                .add(CommonParameters.METHOD_PARAMS, methodParameters2)
                .add(CommonParameters.SLIP, routingSlip)
                .add("condition", ((ConditionalInterface)()-> true))
                .add("shift", -2)
                .build();
        whileActor.execute("whileReturnByShift", message);
        currentStep = routingSlip.next();
        assertEquals(step1, currentStep);
    }

    @Test
    public void whileReturnByShift__checkFalseCase() {
        MethodParameters methodParameters2 = new MethodParameters(
                Arrays.asList("condition", "shift", CommonParameters.SLIP), null
        );
        IStep step1 = new Step(
                "test1",
                "test1",
                new MethodParameters(null, null)
        );
        IStep step2 = new Step(
                "WhileActor",
                "whileReturnByShift",
                methodParameters2
        );
        IRoutingSlip routingSlip = new RoutingSlip("test", step1, step2);
        assertTrue(routingSlip.hasNext());
        IStep currentStep = routingSlip.next();
        assertEquals(step1, currentStep);
        currentStep = routingSlip.next();
        assertEquals(step2, currentStep);
        IActor whileActor = new WhileActor();
        Message message = Message
                .builder()
                .add(CommonParameters.METHOD_PARAMS, methodParameters2)
                .add(CommonParameters.SLIP, routingSlip)
                .add("condition", ((ConditionalInterface)()-> false))
                .add("shift", -2)
                .build();
        whileActor.execute("whileReturnByShift", message);
        assertFalse(routingSlip.hasNext());
    }
}
