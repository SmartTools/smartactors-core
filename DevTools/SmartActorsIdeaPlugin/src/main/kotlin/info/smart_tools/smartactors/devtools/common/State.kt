package info.smart_tools.smartactors.devtools.common

import com.intellij.openapi.project.Project
import info.smart_tools.smartactors.remote_debug_viewer.parser.config.ParsedConfigData
import info.smart_tools.smartactors.remote_debug_viewer.parser.ioc.ParsedIocValue
import info.smart_tools.smartactors.remote_debug_viewer.parser.chain.ParsedChainData

object State {

    private var chainData: List<ParsedChainData> = listOf()
    private var ioc: List<ParsedIocValue> = listOf()
    private var configSections: List<ParsedConfigData> = listOf()

    private lateinit var project: Project

    fun getIocData(): List<ParsedIocValue> {
        return ioc
    }

    fun getConfigSections(): List<ParsedConfigData> {
        return configSections
    }


    fun getChainData(): List<ParsedChainData> {
        return chainData
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

    fun setChainData(parsedChainData: List<ParsedChainData>) {
        this.chainData = parsedChainData
    }
}
