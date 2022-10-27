package info.smart_tools.smartactors.devtools.ioc_viewer.tree

import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.ide.util.treeView.NodeDescriptor
import com.intellij.ide.util.treeView.PresentableNodeDescriptor
import com.intellij.openapi.project.Project

class IocValueNodeDescriptor(
    private val ideaProject: Project,
    private val descriptor: NodeDescriptor<Any>?,
    private val element: AbstractTreeNode<Any>
) : PresentableNodeDescriptor<AbstractTreeNode<Any>>(ideaProject, descriptor) {

    override fun update(presentation: PresentationData) {
        element.update()
        presentation.copyFrom(element.presentation)
    }

    override fun getElement(): AbstractTreeNode<Any> {
        return element
    }
}
