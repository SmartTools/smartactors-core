package info.smart_tools.smartactors.remote_debug_viewer.parser.ioc;

import java.util.List;

public class ParsedIocValue {

    private final String key;
    private final List<ParsedIocStrategy> strategies;

    public ParsedIocValue(final String key, final List<ParsedIocStrategy> strategies) {
        this.key = key;
        this.strategies = strategies;
    }

    public String getKey() {
        return key;
    }

    public List<ParsedIocStrategy> getDependencies() {
        return strategies;
    }
}
