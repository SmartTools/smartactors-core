package info.smart_tools.smartactors.remote_debug_viewer.parser.ioc;

public class ParsedIocModule {

    private final String name;
    private final String version;

    public ParsedIocModule(final String name, final String version) {
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }
}
