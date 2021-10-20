package info.smart_tools.smartactors.devtools.ioc_viewer.tool

class IocViewer {

    fun parseIoc(): List<IocValue> {
        val data = listOf(
            IocValue(
                "key 1",
                listOf(
                    IocDependency("dep 1", "1.0"),
                    IocDependency("dep 2", "2.0")
                )
            ),
            IocValue(
                "key 2",
                listOf(
                    IocDependency("dep 2", "2.0"),
                    IocDependency("dep 3", "3.0")
                )
            )
        )

        return data
    }
}
