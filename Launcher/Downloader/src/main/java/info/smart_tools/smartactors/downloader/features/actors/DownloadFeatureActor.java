package info.smart_tools.smartactors.downloader.features.actors;

import info.smart_tools.smartactors.downloader.Params__DownloadFeature;
import info.smart_tools.smartactors.downloader.Repository;
import info.smart_tools.smartactors.downloader.commons.Executable;
import info.smart_tools.smartactors.downloader.commons.StatelessActor;
import info.smart_tools.smartactors.downloader.feature_downloader.IFeatureDownloader;
import info.smart_tools.smartactors.downloader.features.Feature;
import info.smart_tools.smartactors.downloader.features.FeatureNamespace;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DownloadFeatureActor extends StatelessActor {

    private IFeatureDownloader downloader;

    public DownloadFeatureActor(final IFeatureDownloader downloader) {
        this.downloader = downloader;
    }

    @Executable()
    public Map<String, Object> downloadFeature(
            final Feature feature,
            final List<Repository> repositories,
            final String featureType,
            final File directory
    ) {
        try {
            if (!featureType.equals("json") && !featureType.equals("jar") && !featureType.equals("zip")) {
                throw new RuntimeException("Invalid feature type. Should be one of the following: 'jar', 'zip' or 'json'.");
            }
            Map<String, Object> response = new HashMap<>();
            this.downloader.initialize(null);
            this.downloader.addRepositories(repositories);
            FeatureNamespace featureNamespace = new FeatureNamespace(feature.getFeatureName());
            List<File> result = this.downloader.download(featureNamespace, featureType);
            File file = result != null && !result.isEmpty() ? result.get(0) : null;
            response.put(Params__DownloadFeature.DOWNLOADED_FILE, file);
            if (null != file) {
                System.out.println("Downloaded the feature " + feature.getFeatureName() + ". Feature type: " + featureType + ".");
                response.put(
                        Params__DownloadFeature.CHECK_AND_DELETE_IF_EXIST_FILE_PATH,
                        Paths.get(directory.toString(), file.toPath().getFileName().toString())
                );
                response.put(
                        Params__DownloadFeature.NEW_FILE_FOR_FEATURE_PATH,
                        Paths.get(directory.toString(), file.toPath().getFileName().toString())
                );
                response.put(
                        Params__DownloadFeature.DOWNLOADED_FILE_PATH,
                        file.toPath()
                );
            }

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Could not download feature from remote repository.", e);
        }
    }
}
