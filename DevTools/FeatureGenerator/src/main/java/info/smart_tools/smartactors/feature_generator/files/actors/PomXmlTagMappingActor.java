package info.smart_tools.smartactors.feature_generator.files.actors;

import info.smart_tools.simpleactors.commons.StatelessActor;
import info.smart_tools.simpleactors.commons.annotations.Executable;
import info.smart_tools.smartactors.feature_generator.files.PomXmlTemplateTags;

import java.util.HashMap;
import java.util.Map;

public class PomXmlTagMappingActor extends StatelessActor {

    @Executable
    public Map<String, String> mapTags(final String groupId, final String featureName, final String version) {
        Map<String, String> tags = new HashMap<>();

        tags.put(PomXmlTemplateTags.GROUP_ID, groupId);
        tags.put(PomXmlTemplateTags.FEATURE_NAME, featureName);
        tags.put(PomXmlTemplateTags.VERSION, version);

        return tags;
    }
}
