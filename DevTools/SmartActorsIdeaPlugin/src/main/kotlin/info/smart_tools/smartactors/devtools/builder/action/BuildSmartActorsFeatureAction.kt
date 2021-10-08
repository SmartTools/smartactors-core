package info.smart_tools.smartactors.devtools.builder.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.psi.PsiDirectory
import info.smart_tools.smartactors.devtools.builder.task.BuildFeatureTask

class BuildSmartActorsFeatureAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val featurePath = getActionPath(e)

        BuildFeatureTask(e.project, featurePath).queue()
    }

    private fun getActionPath(e: AnActionEvent): String {
        // TODO: NPE is thrown when using this action from toolbar
        val directory = e.dataContext.getData("psi.Element") as PsiDirectory
        return directory.virtualFile.path
    }
}
