package info.smart_tools.smartactors.feature_generator;

import com.beust.jcommander.JCommander;
import info.smart_tools.simpleactors.SimpleActorsStarter;
import info.smart_tools.smartactors.feature_generator.files.actors.ConfigJsonTagMappingActor;
import info.smart_tools.smartactors.feature_generator.files.actors.CreateFoldersActor;
import info.smart_tools.smartactors.feature_generator.files.actors.PomXmlTagMappingActor;
import info.smart_tools.smartactors.feature_generator.files.actors.TemplateFileCreatorActor;
import info.smart_tools.smartactors.feature_generator.jcommander.Arguments;
import info.smart_tools.smartactors.feature_generator.jcommander.actors.ArgsToMessageActor;

public class FeatureGenerator {

    public static void main(String[] args) throws Exception {
        SimpleActorsStarter starter = new SimpleActorsStarter()
                .addActor("CreateFoldersActor", new CreateFoldersActor())
                .addActor("TemplateFileCreatorActor", new TemplateFileCreatorActor())
                .addActor("ConfigJsonTagMappingActor", new ConfigJsonTagMappingActor())
                .addActor("PomXmlTagMappingActor", new PomXmlTagMappingActor())
                .addActor("ArgsToMessageActor", new ArgsToMessageActor());

        Arguments arguments = new Arguments();
        JCommander.newBuilder()
                .addObject(arguments)
                .build()
                .parse(args);

        starter.start(arguments);
    }
}
