package info.smart_tools.smartactors.devtools.ioc_viewer.tool_window

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

class IocViewerFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val iocViewer = IocViewerToolWindow(toolWindow)
        val contentFactory = ContentFactory.SERVICE.getInstance()
        val content = contentFactory.createContent(iocViewer.getContent(), null, false)
        toolWindow.contentManager.addContent(content)
    }
}
