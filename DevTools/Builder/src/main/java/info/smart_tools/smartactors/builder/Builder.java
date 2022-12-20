package info.smart_tools.smartactors.builder;

import com.beust.jcommander.JCommander;
import info.smart_tools.simpleactors.SimpleActorsStarter;
import info.smart_tools.simpleactors.commons.IArguments;
import info.smart_tools.smartactors.builder.builder.actors.BuilderActor;
import info.smart_tools.smartactors.builder.config.IConfigStrategy;
import info.smart_tools.smartactors.builder.config.actors.ConfigHandlerActor;
import info.smart_tools.smartactors.builder.config.strategies.JsonConfigStrategy;
import info.smart_tools.smartactors.builder.config.strategies.YamlConfigStrategy;
import info.smart_tools.smartactors.builder.feature_builder.MavenFeatureBuilder;
import info.smart_tools.smartactors.builder.jcommander.Arguments;
import info.smart_tools.smartactors.builder.jcommander.actors.ArgsToMessageActor;
import info.smart_tools.smartactors.builder.validator.MavenValidator;
import info.smart_tools.smartactors.builder.validator.actors.ValidatorActor;

import java.util.HashMap;
import java.util.Map;

public class Builder {

    public static void main(String[] args) throws Exception {
        Map<String, IConfigStrategy> configStrategies = new HashMap<>();
        configStrategies.put("json", new JsonConfigStrategy());
        configStrategies.put("yml", new YamlConfigStrategy());

        SimpleActorsStarter starter = new SimpleActorsStarter()
            .addActor("ConfigHandlerActor", new ConfigHandlerActor(configStrategies))
            .addActor("BuilderActor", new BuilderActor(new MavenFeatureBuilder()))
            .addActor("ValidatorActor", new ValidatorActor(new MavenValidator()))
            .addActor("ArgsToMessageActor", new ArgsToMessageActor());

        IArguments arguments = new Arguments();
        JCommander.newBuilder()
            .addObject(arguments)
            .build()
            .parse(args);

        starter.start("builder", arguments);
    }
}
