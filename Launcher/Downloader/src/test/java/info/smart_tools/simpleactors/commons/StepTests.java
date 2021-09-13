package info.smart_tools.simpleactors.commons;

import info.smart_tools.smartactors.downloader.commons.IRoutingSlip;
import info.smart_tools.smartactors.downloader.commons.IStep;
import info.smart_tools.smartactors.downloader.commons.MethodParameters;
import info.smart_tools.smartactors.downloader.commons.RoutingSlip;
import info.smart_tools.smartactors.downloader.commons.Step;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class StepTests {

    @Test
    public void creating() {
        MethodParameters methodParameters = new MethodParameters();
        IStep step = new Step("actorName", "methodName", methodParameters);

        assertEquals("actorName", step.getActorName());
        assertEquals("methodName", step.getMethodName());
        assertEquals(methodParameters, step.getMethodParameters());
    }

    @Test
    public void updating() {
        MethodParameters methodParameters1 = new MethodParameters();
        MethodParameters methodParameters2 = new MethodParameters();
        IStep step = new Step("actorName", "methodName", methodParameters1);
        step.setActorName("testValue1");
        step.setMethodName("testValue2");
        step.setMethodParameters(methodParameters2);
        assertEquals("testValue1", step.getActorName());
        assertEquals("testValue2", step.getMethodName());
        assertEquals(methodParameters2, step.getMethodParameters());
    }
}
