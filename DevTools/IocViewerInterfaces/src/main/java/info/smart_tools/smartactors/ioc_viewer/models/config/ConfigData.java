package info.smart_tools.smartactors.ioc_viewer.models.config;

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
