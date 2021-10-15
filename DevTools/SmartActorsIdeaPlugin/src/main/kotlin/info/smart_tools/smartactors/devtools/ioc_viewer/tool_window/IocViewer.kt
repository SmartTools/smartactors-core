package info.smart_tools.smartactors.devtools.ioc_viewer.tool_window

import com.intellij.openapi.wm.ToolWindow
import javax.swing.JPanel

class IocViewer(toolWindow: ToolWindow) {

    fun getContent(): JPanel {
        return JPanel()
    }
}
