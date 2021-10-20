package info.smart_tools.smartactors.devtools.ioc_viewer.tool

data class IocDependency(val name: String, val version: String)

data class IocValue(val key: String, val dependencies: List<IocDependency>)
