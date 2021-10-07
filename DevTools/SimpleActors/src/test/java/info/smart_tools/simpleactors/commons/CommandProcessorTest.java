package info.smart_tools.simpleactors.commons;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import info.smart_tools.simpleactors.CommandProcessor;
import info.smart_tools.simpleactors.commons.annotations.Executable;
import info.smart_tools.simpleactors.commons.exception.ProcessExecutionException;
import info.smart_tools.simpleactors.commons.json.deserializers.IRoutingDeserializer;
import info.smart_tools.simpleactors.commons.json.deserializers.IStepDeserializer;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CommandProcessorTest {

    static class ExampleActor1 extends StatelessActor {

        @Executable
        public String returnString() {
            System.out.println("stringGetterActor was called");
            return "string";
        }
    }

    static class ExampleActor2 extends StatelessActor {

        @Executable
        public String upperCaseString(String string) {
            System.out.println("upperCaseStringActor was called");
            return string.toUpperCase(Locale.ROOT);
        }
    }

    @Ignore("Issues with file systems, need to fix later")
    @Test
    public void testCommandProcessor__loadAndExecuteRoutingSlipFromResources() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(IRoutingSlip.class, new IRoutingDeserializer());
        module.addDeserializer(IStep.class, new IStepDeserializer());
        mapper.registerModule(module);

        Map<String, IActor> actors = new HashMap<>();
        actors.put("stringGetterActor", new ExampleActor1());
        actors.put("upperCaseStringActor", new ExampleActor2());

        CommandProcessor cp = new CommandProcessor("test", mapper, actors);
        IArguments arguments = new IArguments() {
            @Override
            public String getCommand() {
                return "test_command_processor";
            }
        };

        cp.process(arguments);
    }

    @Ignore("Issues with file systems, need to fix later")
    @Test(expected = ProcessExecutionException.class)
    public void testCommandProcessor__loadNonExistingRoutingSlip() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(IRoutingSlip.class, new IRoutingDeserializer());
        module.addDeserializer(IStep.class, new IStepDeserializer());
        mapper.registerModule(module);

        Map<String, IActor> actors = new HashMap<>();
        actors.put("stringGetterActor", new ExampleActor1());
        actors.put("upperCaseStringActor", new ExampleActor2());

        CommandProcessor cp = new CommandProcessor("test", mapper, actors);
        IArguments arguments = new IArguments() {
            @Override
            public String getCommand() {
                return "non existing routing slip";
            }
        };

        cp.process(arguments);
    }

    @Ignore("Issues with file systems, need to fix later")
    @Test(expected = ProcessExecutionException.class)
    public void testCommandProcessor__attemptToExecuteNonExistingActor() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(IRoutingSlip.class, new IRoutingDeserializer());
        module.addDeserializer(IStep.class, new IStepDeserializer());
        mapper.registerModule(module);

        Map<String, IActor> actors = new HashMap<>();
        actors.put("upperCaseStringActor", new ExampleActor2());

        CommandProcessor cp = new CommandProcessor("test", mapper, actors);
        IArguments arguments = new IArguments() {
            @Override
            public String getCommand() {
                return "test_command_processor";
            }
        };

        cp.process(arguments);
    }
}
