package info.smart_tools.smartactors.devtools.common.server

import info.smart_tools.smartactors.remote_debug_viewer.vm.*
import kotlin.reflect.KClass

class Server(
    hostname: String,
    port: String
) {
    val viewerThread: VMThread

    private val vm = VM().connectTo(hostname, port)
    private val breakpoint: VMBreakpoint
    private lateinit var frame: VMFrame

    init {
        viewerThread = vm.getThread("remote-debug-thread")

        viewerThread.suspend()
        breakpoint = vm.createBreakpoint(viewerThread, "RemoteDebugThread")
        viewerThread.resume()
    }

    fun connect() {
        breakpoint.enable()
        while (!viewerThread.isRunning) {
//            println("Waiting for thread \"" + viewerThread.threadName + "\" to be runnable")
        }
        frame = viewerThread.getFrameByName("RemoteDebugThread")
    }

    fun getFrameObject(): VMObject {
        return frame.thisObject()
    }

    fun disconnect() {
        breakpoint.disable()
        viewerThread.resume()
        vm.disconnect()
    }
}

class SmartActorsServer(private val server: Server) {
    val viewer = server.getFrameObject()

    private val castMapping: Map<KClass<*>, Class<*>> = mapOf<KClass<*>, Class<*>>(
        List::class to MutableList::class.java,
        String::class to String::class.java
    )

    fun VMObject.get(fieldName: String): VMObject {
        return getFieldValue(fieldName).toObject()
    }

    fun VMObject.invoke(methodName: String, vararg arguments: VMValue): VMValue {
        val method = this.getMethod(methodName)
        return method.invoke(server.viewerThread, arguments.toMutableList(), 0)
    }

    infix fun <T> VMValue.castTo(clazz: KClass<*>): T {
        val classToCast = castMapping[clazz] ?: throw RuntimeException("Attempting to cast to unknown class.")

        return this.castTo(classToCast)
    }
}

fun <T> withServer(hostname: String, port: String, initializer: SmartActorsServer.() -> T): T {
    val server = Server(hostname, port)
    server.connect()
    val result = initializer(SmartActorsServer(server))
    server.disconnect()
    return result
}
