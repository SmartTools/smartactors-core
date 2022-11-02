package info.smart_tools.smartactors.remote_debug_viewer.parser.config;

import info.smart_tools.smartactors.remote_debug_viewer.vm.VMObject;
import info.smart_tools.smartactors.remote_debug_viewer.vm.VMValue;

public class ConfigParser {

    public static ParsedConfigData parseConfigData(final VMValue value) {
        VMObject configObject = value.toObject();

        String name = configObject.getFieldValue("name").castTo(String.class);
        String schema = configObject.getFieldValue("schema").castTo(String.class);

        return new ParsedConfigData(name, schema);
    }
}
