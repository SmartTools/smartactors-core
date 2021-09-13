package info.smart_tools.simpleactors.commons.json.deserializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import info.smart_tools.smartactors.downloader.commons.IStep;
import info.smart_tools.smartactors.downloader.commons.MethodParameters;
import info.smart_tools.smartactors.downloader.commons.json.deserializers.IStepDeserializer;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StepDeserizlizerTests {

    ObjectMapper objectMapper;

    @Before
    public void init() {
        this.objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(IStep.class, new IStepDeserializer());
        this.objectMapper.registerModule(module);
    }

    @Test
    public void testDeserialization() throws Exception {
        String stepString = "{\n" +
                "      \"actorName\": \"actorName\",\n" +
                "      \"methodName\": \"methodName\",\n" +
                "      \"methodParameters\": {\n" +
                "        \"argumentPaths\": [\"argument1\", \"argument2\", \"argument3\"],\n" +
                "        \"responsePath\": \"response path\"\n" +
                "      }\n" +
                "    }";
        IStep step = this.objectMapper.readValue(stepString, IStep.class);
        assertEquals("actorName", step.getActorName());
        assertEquals("methodName", step.getMethodName());
        MethodParameters parameters = step.getMethodParameters();
        assertEquals("argument1", parameters.getArgumentPaths().get(0));
        assertEquals("argument2", parameters.getArgumentPaths().get(1));
        assertEquals("argument3", parameters.getArgumentPaths().get(2));
        assertEquals("response path", parameters.getResponsePath());
    }
}
