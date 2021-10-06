package info.smart_tools.smartactors.devtools.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.psi.PsiDirectory
import info.smart_tools.smartactors.devtools.indicator.FeatureBuildingIndicator
import info.smart_tools.smartactors.devtools.task.BuildFeatureTask

class BuildSmartActorsFeatureAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val featurePath = getActionPath(e)

        BuildFeatureTask(e.project, featurePath).queue()
    }

    private fun getActionPath(e: AnActionEvent): String {
        val directory = e.dataContext.getData("psi.Element") as PsiDirectory
        return directory.virtualFile.path
    }
}
