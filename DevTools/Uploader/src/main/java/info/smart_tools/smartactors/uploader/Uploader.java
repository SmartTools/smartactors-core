package info.smart_tools.smartactors.uploader;

import com.beust.jcommander.JCommander;
import info.smart_tools.simpleactors.SimpleActorsStarter;
import info.smart_tools.simpleactors.commons.actors.WhileActor;
import info.smart_tools.smartactors.uploader.feature_uploader.MavenFeatureUploader;
import info.smart_tools.smartactors.uploader.features.actors.CollectFeatureFilesActor;
import info.smart_tools.smartactors.uploader.features.actors.LoadFeatureConfigActor;
import info.smart_tools.smartactors.uploader.files.actors.FileLoadingActor;
import info.smart_tools.smartactors.uploader.jcommander.Arguments;
import info.smart_tools.smartactors.uploader.jcommander.actors.ArgsToMessageActor;
import info.smart_tools.smartactors.uploader.repositories.actors.CreateRepositoryConfigActor;
import info.smart_tools.smartactors.uploader.repositories.actors.RepositoryUploadActor;

public class Uploader {

    public static void main(final String[] args) throws Exception {
        SimpleActorsStarter starter = new SimpleActorsStarter()
                .addActor("WhileActor", new WhileActor())
                .addActor("FileLoadingActor", new FileLoadingActor("*.{jar,zip}"))
                .addActor("LoadFeatureConfigActor", new LoadFeatureConfigActor())
                .addActor("CreateRepositoryConfigActor", new CreateRepositoryConfigActor())
                .addActor("CollectFeatureFilesActor", new CollectFeatureFilesActor())
                .addActor("RepositoryUploadActor", new RepositoryUploadActor(new MavenFeatureUploader()))
                .addActor("ArgsToMessageActor", new ArgsToMessageActor());

        Arguments arguments = new Arguments();
        JCommander.newBuilder()
                .addObject(arguments)
                .build()
                .parse(args);

        starter.start(arguments);
    }
}
