package info.smart_tools.smartactors.remote_debug_viewer.parser.config;

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

    @Override
    public String toString() {
        return "ParsedConfigData{" +
                "name='" + name + '\'' +
                ", schema='" + schema + '\'' +
                '}';
    }
}
