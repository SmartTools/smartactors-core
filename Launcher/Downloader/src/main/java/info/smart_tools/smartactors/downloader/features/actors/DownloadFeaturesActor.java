package info.smart_tools.smartactors.downloader.features.actors;

import info.smart_tools.smartactors.downloader.commons.Executable;
import info.smart_tools.smartactors.downloader.commons.StatefulActor;
import info.smart_tools.smartactors.downloader.commons.actors.ConditionalInterface;
import info.smart_tools.smartactors.downloader.features.Feature;

import java.util.List;

public class DownloadFeaturesActor extends StatefulActor {

    private int size;
    private int current;
    private List<Feature> features;

    @Executable()
    public ConditionalInterface configure(final List<Feature> features) {
        this.features = features;
        this.size = features.size();
        this.current = 0;
        System.out.println("Start to download features.");
        return () -> current < size;
    }

    @Executable()
    public Feature nextFeature() {
        try {
            Feature feature = this.features.get(this.current);
            System.out.println("Progress: " + (int)(((double) this.current) / ((double) this.size) * 100) + "%.");
            this.current++;
            return feature;
        } catch (Exception e) {
            throw new RuntimeException("Could not download features from remote repository.", e);
        }
    }
}
