package info.smart_tools.smartactors.builder;

import com.beust.jcommander.JCommander;
import info.smart_tools.simpleactors.SimpleActorsStarter;
import info.smart_tools.simpleactors.commons.IArguments;
import info.smart_tools.smartactors.builder.builder.actors.BuilderActor;
import info.smart_tools.smartactors.builder.feature_builder.MavenFeatureBuilder;
import info.smart_tools.smartactors.builder.jcommander.Arguments;
import info.smart_tools.smartactors.builder.jcommander.actors.ArgsToMessageActor;
import info.smart_tools.smartactors.builder.validator.MavenValidator;
import info.smart_tools.smartactors.builder.validator.actors.ValidatorActor;

public class Builder {

    public static void main(String[] args) throws Exception {
        SimpleActorsStarter starter = new SimpleActorsStarter()
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
