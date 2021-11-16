package info.smart_tools.smartactors.devtools.completion

import info.smart_tools.smartactors.devtools.common.State
import info.smart_tools.smartactors.remote_debug_viewer.parser.config.ParsedConfigData

class ConfigurationManager {

    fun getConfigSections(): List<ConfigData> {
        return State.getConfigSections().map(::parseConfig)
    }

    private fun parseConfig(config: ParsedConfigData): ConfigData {
        return ConfigData(config.name, config.schema)
    }
}
