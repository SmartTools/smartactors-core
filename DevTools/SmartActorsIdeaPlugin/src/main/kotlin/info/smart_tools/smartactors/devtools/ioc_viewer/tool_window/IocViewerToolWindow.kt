package info.smart_tools.smartactors.devtools.ioc_viewer.tool_window

import com.intellij.icons.AllIcons
import com.intellij.ide.dnd.aware.DnDAwareTree
import com.intellij.ide.util.treeView.NodeRenderer
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.ToolbarDecorator
import info.smart_tools.smartactors.devtools.ioc_viewer.tool.IocValue
import info.smart_tools.smartactors.devtools.ioc_viewer.tool.IocViewer
import javax.swing.JPanel
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.MutableTreeNode

class IocViewerToolWindow(toolWindow: ToolWindow) {

    fun getContent(): JPanel {
        val iocViewer = IocViewer()

        val rootNode = DefaultMutableTreeNode("ioc_viewer.root")
        iocViewer.parseIoc().map { parseIocValue(it) }.forEach { rootNode.add(it) }

        val treeModel = DefaultTreeModel(rootNode, false)
        val treeList = DnDAwareTree(treeModel).apply {
            isEditable = false
            isRootVisible = false
            showsRootHandles = true
            isLargeModel = true

            cellRenderer = IocValueCellRenderer()
        }

        val toolbarDecorator = ToolbarDecorator.createDecorator(treeList)
            .initPosition()
            .disableAddAction()
            .disableRemoveAction()
            .disableDownAction()
            .disableUpAction()

        return toolbarDecorator.createPanel()
    }
}

fun parseIocValue(value: IocValue): MutableTreeNode {
    val iocValueNode = DefaultMutableTreeNode(value.key)

    value.strategies.forEach { strategy ->
        val module = strategy.module
        val strategyName = strategy.strategy

        val moduleNode = DefaultMutableTreeNode(module.name)
        moduleNode.add(DefaultMutableTreeNode("Name: " + module.name))
        moduleNode.add(DefaultMutableTreeNode("Version: " + module.version))
        moduleNode.add(DefaultMutableTreeNode("Strategy Type: $strategyName"))

        iocValueNode.add(moduleNode)
    }

    return iocValueNode
}

class IocValueCellRenderer : NodeRenderer() {
    override fun customizeCellRenderer(
        tree: JTree,
        value: Any?,
        selected: Boolean,
        expanded: Boolean,
        leaf: Boolean,
        row: Int,
        hasFocus: Boolean
    ) {
        super.customizeCellRenderer(tree, value, selected, expanded, leaf, row, hasFocus)

        if (value !is DefaultMutableTreeNode) {
            return
        }

        val node: DefaultMutableTreeNode = value
    }
}