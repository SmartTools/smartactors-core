package info.smart_tools.smartactors.remote_debug.models.config;

public class ConfigData {

    private final String name;
    private final String schema;

    public ConfigData(final String name, final String schema) {
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
