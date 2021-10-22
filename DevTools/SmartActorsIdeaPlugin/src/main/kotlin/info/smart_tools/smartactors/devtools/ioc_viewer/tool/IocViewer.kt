package info.smart_tools.smartactors.devtools.ioc_viewer.tool

import info.smart_tools.smartactors.ioc_viewer.parser.IocParser
import info.smart_tools.smartactors.ioc_viewer.parser.ParsedIocModule
import info.smart_tools.smartactors.ioc_viewer.parser.ParsedIocStrategy
import info.smart_tools.smartactors.ioc_viewer.parser.ParsedIocValue
import info.smart_tools.smartactors.ioc_viewer.vm.VM
import info.smart_tools.smartactors.ioc_viewer.vm.VMValue

class IocViewer {

    fun parseIoc(): List<IocValue> {
        // TODO: add error handling
        val vm = VM().connectTo("localhost", "5001")

        val viewerThread = vm.getThread("ioc-viewer-thread")
        viewerThread.suspend()

        val breakpoint = vm.createBreakpoint(viewerThread, "IocViewerThread")
        viewerThread.resume()

        // TODO: add loop {
        breakpoint.enable()
        // TODO: this should be done in background, otherwise it will freeze UI
        while (!viewerThread.isRunning) {
            println("Waiting for thread \"" + viewerThread.threadName + "\" to be runnable")
        }
        val frame = viewerThread.getFrameByName("IocViewerThread")
        val frameObject = frame.thisObject()
        val parseIocMethod = frameObject.getMethod("parseIoc")
        val iocValues = parseIocMethod.invoke(viewerThread, ArrayList(), 0).castTo<List<VMValue>>(
            MutableList::class.java
        )

        val ioc = iocValues.map(IocParser::parseIocValue)

        breakpoint.disable()
        viewerThread.resume()
        // TODO: add loop }

        return ioc.map(::parseValue)
    }
}

fun parseModule(module: ParsedIocModule): IocModule {
    val name = module.name
    val version = module.version

    return IocModule(name, version)
}

fun parseStrategy(strategy: ParsedIocStrategy): IocStrategy {
    val module = strategy.module
    val dependency = strategy.dependency

    return IocStrategy(parseModule(module), dependency)
}

fun parseValue(value: ParsedIocValue): IocValue {
    val key = value.key
    val dependencies = value.dependencies.map(::parseStrategy)

    return IocValue(key, dependencies)
}
