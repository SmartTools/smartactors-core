package info.smart_tools.smartactors.devtools.completion

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import info.smart_tools.smartactors.devtools.common.State
import info.smart_tools.smartactors.devtools.common.server.withServer
import info.smart_tools.smartactors.ioc_viewer.parser.config.ConfigParser
import info.smart_tools.smartactors.ioc_viewer.parser.ioc.IocParser
import info.smart_tools.smartactors.ioc_viewer.vm.VMValue

class SmartActorsPluginStartup : StartupActivity {

    override fun runActivity(project: Project) {
        withServer("localhost", "5001") {
            val iocData: List<VMValue> = viewer.invoke("parseIoc") castTo List::class
            val parsedIocData = iocData.map(IocParser::parseIocValue)
            State.setIocData(parsedIocData)

            val loadedConfigs: List<VMValue> = viewer.invoke("getConfigSections") castTo List::class
            val parsedConfigData = loadedConfigs.map(ConfigParser::parseConfigData)
            State.setConfigSections(parsedConfigData)
            println("Acquired data from server JVM.")
        }

        State.setProject(project)
    }
}
