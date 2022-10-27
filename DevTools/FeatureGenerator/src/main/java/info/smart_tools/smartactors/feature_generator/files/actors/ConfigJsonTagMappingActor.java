package info.smart_tools.smartactors.feature_generator.files.actors;

import info.smart_tools.simpleactors.commons.StatelessActor;
import info.smart_tools.simpleactors.commons.annotations.Executable;
import info.smart_tools.smartactors.feature_generator.files.ConfigJsonTemplateTags;

import java.util.HashMap;
import java.util.Map;

public class ConfigJsonTagMappingActor extends StatelessActor {

    @Executable
    public Map<String, String> mapTags(final String groupId, final String featureName, final String version) {
        Map<String, String> tags = new HashMap<>();

        tags.put(ConfigJsonTemplateTags.FEATURE_NAME, groupId + ":" + featureName + ":" + version);

        return tags;
    }
}
