package info.smart_tools.smartactors.feature_generator.files.actors;

import info.smart_tools.smartactors.feature_generator.files.ConfigJsonTemplateTags;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ConfigJsonTagMappingActorTest {

    @Test
    public void configJsonTagMapping__mapTags() {
        String groupId = "group";
        String name = "feature";
        String version = "0.1.0";

        ConfigJsonTagMappingActor actor = new ConfigJsonTagMappingActor();
        Map<String, String> tags = actor.mapTags(groupId, name, version);

        assertEquals("group:feature:0.1.0", tags.get(ConfigJsonTemplateTags.FEATURE_NAME));
    }
}
