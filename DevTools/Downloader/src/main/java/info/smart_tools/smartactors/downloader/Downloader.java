package info.smart_tools.smartactors.downloader;

import com.beust.jcommander.JCommander;
import info.smart_tools.simpleactors.SimpleActorsStarter;
import info.smart_tools.simpleactors.commons.actors.InsertRoutingSlipActor;
import info.smart_tools.simpleactors.commons.actors.WhileActor;
import info.smart_tools.smartactors.downloader.feature_downloader.AetherArtifactResolver;
import info.smart_tools.smartactors.downloader.feature_downloader.IFeatureDownloader;
import info.smart_tools.smartactors.downloader.features.actors.CollectDependencyDataActor;
import info.smart_tools.smartactors.downloader.features.actors.DownloadFeatureActor;
import info.smart_tools.smartactors.downloader.features.actors.DownloadFeaturesActor;
import info.smart_tools.smartactors.downloader.features.actors.FeaturesAndRepositoriesStorageActor;
import info.smart_tools.smartactors.downloader.features.actors.ReadFeatureConfigActor;
import info.smart_tools.smartactors.downloader.files.actors.FileOperationsActor;
import info.smart_tools.smartactors.downloader.jcommander.Args;
import info.smart_tools.smartactors.downloader.jcommander.actors.ArgsToMessageActor;

public class Downloader {

    public static void main(final String[] args) throws Exception {
        IFeatureDownloader downloader = new AetherArtifactResolver();
        downloader.initialize(null);
        SimpleActorsStarter starter = new SimpleActorsStarter()
            .addActor("fileOperationActor", new FileOperationsActor())
            .addActor("readFeatureConfigActor", new ReadFeatureConfigActor())
            .addActor("downloadFeaturesActor", new DownloadFeaturesActor())
            .addActor("downloadFeatureActor", new DownloadFeatureActor(downloader))
            .addActor("collectDependencyDataActor", new CollectDependencyDataActor())
            .addActor("ArgsToMessageActor", new ArgsToMessageActor())
            .addActor("whileActor", new WhileActor())
            .addActor("insertRoutingSlipActor", new InsertRoutingSlipActor())
            .addActor("featuresAndRepositoriesStoragesActor", new FeaturesAndRepositoriesStorageActor());

        Args arguments = new Args();
        JCommander
                .newBuilder()
                .addObject(arguments)
                .build()
                .parse(args);

        starter.start("downloader", arguments);
    }
}
