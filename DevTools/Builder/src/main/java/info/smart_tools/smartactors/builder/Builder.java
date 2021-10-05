package info.smart_tools.smartactors.builder;

import com.beust.jcommander.JCommander;
import info.smart_tools.simpleactors.SimpleActorsStarter;
import info.smart_tools.simpleactors.commons.IArguments;
import info.smart_tools.smartactors.builder.builder.actors.BuilderActor;
import info.smart_tools.smartactors.builder.feature_builder.MavenFeatureBuilder;
import info.smart_tools.smartactors.builder.jcommander.Arguments;
import info.smart_tools.smartactors.builder.jcommander.actors.ArgsToMessageActor;

public class Builder {

    public static void main(String[] args) throws Exception {
        SimpleActorsStarter starter = new SimpleActorsStarter()
                .addActor("BuilderActor", new BuilderActor(new MavenFeatureBuilder()))
                .addActor("ArgsToMessageActor", new ArgsToMessageActor());

        IArguments arguments = new Arguments();
        JCommander.newBuilder()
                .addObject(arguments)
                .build()
                .parse(args);

        starter.start(arguments);
    }
}
