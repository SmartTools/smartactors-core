package info.smart_tools.smartactors.feature_generator.files.actors;

import info.smart_tools.smartactors.feature_generator.files.PomXmlTemplateTags;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class PomXmlTagMappingActorTest {

    @Test
    public void pomXmlTagMapping__mapTags() {
        String groupId = "group";
        String name = "feature";
        String version = "0.1.0";

        PomXmlTagMappingActor actor = new PomXmlTagMappingActor();
        Map<String, String> tags = actor.mapTags(groupId, name, version);

        assertEquals("group", tags.get(PomXmlTemplateTags.GROUP_ID));
        assertEquals("feature", tags.get(PomXmlTemplateTags.FEATURE_NAME));
        assertEquals("0.1.0", tags.get(PomXmlTemplateTags.VERSION));
    }
}
