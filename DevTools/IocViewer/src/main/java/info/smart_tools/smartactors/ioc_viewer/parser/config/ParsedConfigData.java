package info.smart_tools.smartactors.ioc_viewer.parser.config;

public class ParsedConfigData {

    private final String name;
    private final String schema;

    public ParsedConfigData(final String name, final String schema) {
        this.name = name;
        this.schema = schema;
    }

    public String getName() {
        return name;
    }

    public String getSchema() {
        return schema;
    }
}
