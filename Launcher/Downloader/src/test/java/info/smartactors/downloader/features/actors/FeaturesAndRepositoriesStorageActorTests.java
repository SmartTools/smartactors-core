package info.smartactors.downloader.features.actors;

import info.smart_tools.smartactors.downloader.Repository;
import info.smart_tools.smartactors.downloader.commons.CommonParameters;
import info.smart_tools.smartactors.downloader.commons.IMessage;
import info.smart_tools.smartactors.downloader.commons.Message;
import info.smart_tools.smartactors.downloader.commons.MethodParameters;
import info.smart_tools.smartactors.downloader.features.Feature;
import info.smart_tools.smartactors.downloader.features.actors.FeaturesAndRepositoriesStorageActor;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FeaturesAndRepositoriesStorageActorTests {

    @Test
    public void creating() {
        FeaturesAndRepositoriesStorageActor actor = new FeaturesAndRepositoriesStorageActor();
        assertNotNull(actor);
    }

    @Test
    public void usingTheConfigureMethod() {
        FeaturesAndRepositoriesStorageActor actor = new FeaturesAndRepositoriesStorageActor();
        MethodParameters methodParameters = new MethodParameters(
                Collections.emptyList(), CommonParameters.SPLIT_RESPONSE
        );
        IMessage message = Message
                .builder()
                .add(CommonParameters.METHOD_PARAMS, methodParameters)
                .build();
        actor.execute("configure", message);
        assertNotNull(message.get(CommonParameters.FEATURES));
        assertNotNull(message.get(CommonParameters.REPOSITORIES));
    }

    @Test
    public void usingTheUpdateStoragesMethod() {
        FeaturesAndRepositoriesStorageActor actor = new FeaturesAndRepositoriesStorageActor();
        List<Feature> features = new ArrayList<>();
        List<Repository> repositories = new ArrayList<>();
        Feature feature1 = new Feature("feature 1");
        Repository repository1 = new Repository();
        feature1.setRepository(repository1);
        actor.updateStorages(feature1, features, repositories);
        assertEquals(1, features.size());
        assertEquals(1, repositories.size());
        assertEquals(feature1, features.get(0));
        assertEquals(repository1, repositories.get(0));
        actor.updateStorages(null, features, repositories);
        actor.updateStorages(feature1, null, null);
        assertEquals(1, features.size());
        assertEquals(1, repositories.size());
        assertEquals(feature1, features.get(0));
        assertEquals(repository1, repositories.get(0));
        actor.updateStorages(feature1, features, repositories);
        assertEquals(1, features.size());
        assertEquals(1, repositories.size());
        assertEquals(feature1, features.get(0));
        assertEquals(repository1, repositories.get(0));
    }
}
