package info.smart_tools.smartactors.uploader.repositories.actors;

import info.smart_tools.simpleactors.commons.StatelessActor;
import info.smart_tools.simpleactors.commons.annotations.Executable;
import info.smart_tools.smartactors.uploader.feature_uploader.IFeatureUploader;
import info.smart_tools.smartactors.uploader.features.Feature;
import info.smart_tools.smartactors.uploader.repositories.RepositoryConfig;

import java.io.File;

public class RepositoryUploadActor extends StatelessActor {

    private final IFeatureUploader featureUploader;

    public RepositoryUploadActor(final IFeatureUploader featureUploader) {
        this.featureUploader = featureUploader;
    }

    @Executable
    public void upload(
            final File file,
            final Feature feature,
            final RepositoryConfig repositoryConfig,
            final String username,
            final String password
    ) {
        System.out.printf(
                "Uploading file \"%s\" for feature \"%s\" to %s\n",
                file.getName(),
                feature.getFeatureName(),
                repositoryConfig.getRepositoryUrl()
        );
        Boolean uploadSuccess = this.featureUploader.upload(file, feature, repositoryConfig, username, password);
        if (uploadSuccess) {
            System.out.printf("File \"%s\" was uploaded\n", file.getName());
        } else {
            System.out.printf("Failed to upload file \"%s\", refer to stack trace to diagnose what went wrong\n", file.getName());
        }
    }
}
