package info.smart_tools.smartactors.downloader;

import com.beust.jcommander.JCommander;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import info.smart_tools.smartactors.downloader.commons.IRoutingSlip;
import info.smart_tools.smartactors.downloader.commons.IStep;
import info.smart_tools.smartactors.downloader.commons.json.deserializers.IRoutingDeserializer;
import info.smart_tools.smartactors.downloader.commons.json.deserializers.IStepDeserializer;
import info.smart_tools.smartactors.downloader.jcommander.Args;

public class Downloader {

    private static ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(IRoutingSlip.class, new IRoutingDeserializer());
        module.addDeserializer(IStep.class, new IStepDeserializer());
        mapper.registerModule(module);
    }

    public static void main(final String[] args) {
        Args arguments = new Args();
        JCommander
                .newBuilder()
                .addObject(arguments)
                .build()
                .parse(args);
        CommandProcessor cp = new CommandProcessor(mapper);
        cp.process(arguments);
    }
}
