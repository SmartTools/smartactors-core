package info.smart_tools.smartactors.downloader.features.actors;

import info.smart_tools.simpleactors.commons.CommonParameters;
import info.smart_tools.simpleactors.commons.IMessage;
import info.smart_tools.simpleactors.commons.Message;
import info.smart_tools.simpleactors.commons.MethodParameters;
import info.smart_tools.smartactors.downloader.Params__DownloadFeature;
import info.smart_tools.smartactors.downloader.Repository;
import info.smart_tools.smartactors.downloader.features.Feature;
import org.junit.Test;

import java.net.URL;
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
        assertNotNull(message.get(Params__DownloadFeature.FEATURES));
        assertNotNull(message.get(Params__DownloadFeature.REPOSITORIES));
    }

    @Test
    public void usingTheUpdateStoragesMethod() throws Exception {
        FeaturesAndRepositoriesStorageActor actor = new FeaturesAndRepositoriesStorageActor();
        List<Feature> features = new ArrayList<>();
        List<Repository> repositories = new ArrayList<>();
        Feature feature1 = new Feature("feature 1");
        Repository repository1 = new Repository("id1", new URL("https://info.smart_tools.smartactors1"));
        Repository repository2 = new Repository("id2", new URL("https://info.smart_tools.smartactors2"));
        Repository repository3 = new Repository("id3", new URL("https://info.smart_tools.smartactors3"));
        feature1.setRepository(repository1);
        feature1.setDependencyRepositories(
                new ArrayList<Repository>(){{add(repository2); add(repository3);}}
        );
        actor.updateStorages(feature1, features, repositories);
        assertEquals(1, features.size());
        assertEquals(3, repositories.size());
        assertEquals(feature1, features.get(0));
        assertEquals(repository1, repositories.get(0));
        assertEquals(repository2, repositories.get(1));
        assertEquals(repository3, repositories.get(2));
        actor.updateStorages(null, features, repositories);
        actor.updateStorages(feature1, null, null);
        assertEquals(1, features.size());
        assertEquals(3, repositories.size());
        assertEquals(feature1, features.get(0));
        assertEquals(repository1, repositories.get(0));
        assertEquals(repository2, repositories.get(1));
        assertEquals(repository3, repositories.get(2));
        actor.updateStorages(feature1, features, repositories);
        assertEquals(1, features.size());
        assertEquals(3, repositories.size());
        assertEquals(feature1, features.get(0));
        assertEquals(repository1, repositories.get(0));
        assertEquals(repository2, repositories.get(1));
        assertEquals(repository3, repositories.get(2));
    }
}
