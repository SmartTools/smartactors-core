package info.smart_tools.smartactors.devtools.ioc_viewer.tool

data class IocModule(val name: String, val version: String)

data class IocStrategy(val module: IocModule, val strategy: Any)

data class IocValue(val key: String, val strategies: List<IocStrategy>)
