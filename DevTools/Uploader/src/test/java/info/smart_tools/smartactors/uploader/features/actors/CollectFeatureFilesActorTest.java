package info.smart_tools.smartactors.uploader.features.actors;

import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CollectFeatureFilesActorTest {

    @Test
    public void collectFeatureFiles__configureAndPerformProcessing() {
        File fileJar = new File("features/simple-feature.jar");
        File fileZip = new File("features/simple-feature.zip");

        CollectFeatureFilesActor actor = new CollectFeatureFilesActor();
        actor.config(new ArrayList<File>() {{
            add(fileJar);
            add(fileZip);
        }});

        assertTrue(actor.checkRemainingFiles().compare());
        File firstFile = actor.processNextFile();
        assertNotNull(firstFile);

        assertTrue(actor.checkRemainingFiles().compare());
        File secondFile = actor.processNextFile();
        assertNotNull(secondFile);

        assertFalse(actor.checkRemainingFiles().compare());
    }
}
