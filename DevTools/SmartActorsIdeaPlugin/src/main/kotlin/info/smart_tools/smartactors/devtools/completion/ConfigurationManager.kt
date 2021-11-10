package info.smart_tools.smartactors.devtools.completion

import com.intellij.json.JsonLanguage
import com.intellij.psi.PsiFileFactory
import info.smart_tools.smartactors.devtools.common.State
import info.smart_tools.smartactors.ioc_viewer.parser.config.ParsedConfigData

class ConfigurationManager {

    private val project = State.getProject()

    fun getConfigSections(): List<ConfigData> {
        return State.getConfigSections().map(::parseConfig)
    }

    private fun parseConfig(config: ParsedConfigData): ConfigData {
//        val schemaPsiFile = if (config.schema == null) null else {
//            PsiFileFactory.getInstance(project)
//                .createFileFromText(config.name + "_schema", JsonLanguage.INSTANCE, config.schema)
//        }

        return ConfigData(config.name, config.schema)
    }
}
