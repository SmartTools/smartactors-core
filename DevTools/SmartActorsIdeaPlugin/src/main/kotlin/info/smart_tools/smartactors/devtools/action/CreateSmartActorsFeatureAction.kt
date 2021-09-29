package info.smart_tools.smartactors.devtools.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.psi.PsiDirectory
import info.smart_tools.smartactors.devtools.dialog.NewSmartActorsFeatureDialog

class CreateSmartActorsFeatureAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        NewSmartActorsFeatureDialog(e.project!!, getActionPath(e)).showAndGet()
    }

    private fun getActionPath(e: AnActionEvent): String {
        val directory = e.dataContext.getData("psi.Element") as PsiDirectory
        return directory.virtualFile.path
    }
}
