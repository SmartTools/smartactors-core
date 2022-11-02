package info.smart_tools.smartactors.uploader.features.actors;

import info.smart_tools.simpleactors.commons.StatefulActor;
import info.smart_tools.simpleactors.commons.actors.ConditionalInterface;
import info.smart_tools.simpleactors.commons.annotations.Executable;

import java.io.File;
import java.util.List;

public class CollectFeatureFilesActor extends StatefulActor {

    private List<File> uploadFiles;

    @Executable
    public void config(final List<File> files) {
        this.uploadFiles = files;
    }

    @Executable
    public ConditionalInterface checkRemainingFiles() {
        return () -> uploadFiles.size() > 0;
    }

    @Executable
    public File processNextFile() {
        try {
            return this.uploadFiles.remove(0);
        } catch (Exception e) {
            throw new RuntimeException("Could not collect feature files for upload.", e);
        }
    }
}
