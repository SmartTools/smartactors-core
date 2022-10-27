package info.smart_tools.simpleactors.commons;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MethodParametersTests {

    @Test
    public void creating() {
        MethodParameters methodParameters = new MethodParameters(Arrays.asList("1", "2"), "3");
        assertEquals("1", methodParameters.getArgumentPaths().get(0));
        assertEquals("2", methodParameters.getArgumentPaths().get(1));
        assertEquals("3", methodParameters.getResponsePath());
    }

    @Test
    public void creatingByDefaultConstructor() {
        MethodParameters methodParameters = new MethodParameters();
        assertNull(methodParameters.getArgumentPaths());
        assertNull(methodParameters.getResponsePath());
    }

    @Test
    public void changing() {
        MethodParameters methodParameters = new MethodParameters(Arrays.asList("1", "2"), "3");
        methodParameters.setResponsePath("6");
        methodParameters.setArgumentPaths(Arrays.asList("4", "5"));
        assertEquals("4", methodParameters.getArgumentPaths().get(0));
        assertEquals("5", methodParameters.getArgumentPaths().get(1));
        assertEquals("6", methodParameters.getResponsePath());
    }
}
