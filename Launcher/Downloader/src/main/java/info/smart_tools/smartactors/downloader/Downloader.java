package info.smart_tools.smartactors.downloader;

import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import java.io.File;

public class Downloader {
    public static void main(final String[] args)
            throws Exception {
        Thread.sleep(5000);
        File[] result = Maven
                .configureResolver()
                .withRemoteRepo(
                        "smartactors_core_and_core_features_dev",
                        "https://repository.smart-tools.info/artifactory/smartactors_core_and_core_features_dev",
                        "default"
                )
                .resolve("info.smart_tools.smartactors:BaseExceptions:json:0.7.0")
                .withoutTransitivity()
                .asFile();

        Thread.sleep(5000);
    }
}
