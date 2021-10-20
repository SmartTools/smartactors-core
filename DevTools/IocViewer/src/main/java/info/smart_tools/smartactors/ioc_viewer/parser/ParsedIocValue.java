package info.smart_tools.smartactors.ioc_viewer.parser;

import java.util.List;

public class ParsedIocValue {

    private final String key;
    private final List<ParsedIocDependency> dependencies;

    public ParsedIocValue(final String key, final List<ParsedIocDependency> dependencies) {
        this.key = key;
        this.dependencies = dependencies;
    }

    public String getKey() {
        return key;
    }

    public List<ParsedIocDependency> getDependencies() {
        return dependencies;
    }
}
