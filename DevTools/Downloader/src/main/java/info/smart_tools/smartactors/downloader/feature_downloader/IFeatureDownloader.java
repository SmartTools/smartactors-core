package info.smart_tools.smartactors.downloader.feature_downloader;

import info.smart_tools.smartactors.downloader.Repository;
import info.smart_tools.smartactors.downloader.features.FeatureNamespace;

import java.io.File;
import java.util.List;

public interface IFeatureDownloader {

    public void initialize(Object object);

    public void addRepository(final Repository repository);

    public void addRepositories(final List<Repository> repositories);

    public List<File> download(final FeatureNamespace featureNamespace, final String featureType);
}
