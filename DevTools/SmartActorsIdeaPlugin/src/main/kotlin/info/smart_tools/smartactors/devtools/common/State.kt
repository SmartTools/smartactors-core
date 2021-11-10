package info.smart_tools.smartactors.devtools.common

import com.intellij.openapi.project.Project
import info.smart_tools.smartactors.ioc_viewer.parser.config.ParsedConfigData
import info.smart_tools.smartactors.ioc_viewer.parser.ioc.ParsedIocValue

object State {

    private var ioc: List<ParsedIocValue> = listOf()
    private var configSections: List<ParsedConfigData> = listOf()

    private lateinit var project: Project

    fun getIocData(): List<ParsedIocValue> {
        return ioc
    }

    fun getConfigSections(): List<ParsedConfigData> {
        return configSections
    }

    fun getProject(): Project {
        return project
    }

    fun setIocData(ioc: List<ParsedIocValue>) {
        this.ioc = ioc
    }

    fun setConfigSections(configSections: List<ParsedConfigData>) {
        this.configSections = configSections
    }

    fun setProject(project: Project) {
        this.project = project
    }
}
