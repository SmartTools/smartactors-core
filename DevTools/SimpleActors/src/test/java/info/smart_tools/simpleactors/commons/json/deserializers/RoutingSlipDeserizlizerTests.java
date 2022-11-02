package info.smart_tools.simpleactors.commons.json.deserializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import info.smart_tools.simpleactors.commons.IRoutingSlip;
import info.smart_tools.simpleactors.commons.IStep;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class RoutingSlipDeserizlizerTests {

    ObjectMapper objectMapper;

    @Before
    public void init() {
        this.objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(IStep.class, new IStepDeserializer());
        module.addDeserializer(IRoutingSlip.class, new IRoutingDeserializer());
        this.objectMapper.registerModule(module);
    }

    @Test
    public void testDeserialization_case1() throws Exception {
        String slipString = "{\n" +
                "  \"name\": \"slip1\",\n" +
                "  \"steps\": [\n" +
                "    {\n" +
                "      \"actorName\": \"actorName1\",\n" +
                "      \"methodName\": \"methodName1\",\n" +
                "      \"methodParameters\": {\n" +
                "        \"argumentPaths\": [\"argument1\"],\n" +
                "        \"responsePath\": \"responsePath\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}\n" +
                "\n";
        IRoutingSlip slip = objectMapper.readValue(slipString, IRoutingSlip.class);
        assertEquals("slip1", slip.getName());
        assertTrue(slip.hasNext());
        IStep step = slip.next();
        assertFalse(slip.hasNext());
        assertNotNull(step);
    }

    @Test
    public void testDeserialization_case2() throws Exception {
        String slipString = "{\n" +
                "  \"name\": \"slip1\",\n" +
                "  \"steps\": null" +
                "}\n" +
                "\n";
        IRoutingSlip slip = objectMapper.readValue(slipString, IRoutingSlip.class);
        assertEquals("slip1", slip.getName());
        assertFalse(slip.hasNext());
    }
}
