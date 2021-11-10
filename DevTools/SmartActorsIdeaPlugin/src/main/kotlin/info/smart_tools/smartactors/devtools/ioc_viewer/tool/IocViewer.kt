package info.smart_tools.smartactors.devtools.ioc_viewer.tool

import info.smart_tools.smartactors.devtools.common.State
import info.smart_tools.smartactors.ioc_viewer.parser.ioc.ParsedIocModule
import info.smart_tools.smartactors.ioc_viewer.parser.ioc.ParsedIocStrategy
import info.smart_tools.smartactors.ioc_viewer.parser.ioc.ParsedIocValue

class IocViewer {

    fun parseIoc(): List<IocValue> {
        return State.getIocData().map(::parseValue)
    }

    private fun parseModule(module: ParsedIocModule): IocModule {
        val name = module.name
        val version = module.version

        return IocModule(name, version)
    }

    private fun parseStrategy(strategy: ParsedIocStrategy): IocStrategy {
        val module = strategy.module
        val dependency = strategy.dependency

        return IocStrategy(parseModule(module), dependency)
    }

    private fun parseValue(value: ParsedIocValue): IocValue {
        val key = value.key
        val dependencies = value.dependencies.map(::parseStrategy)

        return IocValue(key, dependencies)
    }
}
