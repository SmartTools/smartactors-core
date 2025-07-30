package info.smart_tools.smartactors.uploader.feature_uploader;

import info.smart_tools.smartactors.uploader.features.Feature;
import info.smart_tools.smartactors.uploader.repositories.RepositoryConfig;

import java.io.File;

public interface IFeatureUploader {

    Boolean upload(File file, Feature feature, RepositoryConfig repositoryConfig, String username, String password);
}
