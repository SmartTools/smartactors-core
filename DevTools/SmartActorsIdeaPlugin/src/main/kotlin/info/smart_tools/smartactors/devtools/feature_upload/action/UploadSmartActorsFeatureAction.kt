package info.smart_tools.smartactors.devtools.feature_upload.action

import com.intellij.json.psi.JsonFile
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.MessageDialogBuilder
import com.intellij.psi.PsiDirectory
import info.smart_tools.smartactors.devtools.feature_upload.dialog.UploadSmartActorsFeatureDialog
import info.smart_tools.smartactors.devtools.common.feature.parseFeature

class UploadSmartActorsFeatureAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val feature = getConfigJson(e)?.parseFeature()
        if (feature == null) {
            MessageDialogBuilder
                .okCancel("Failed to upload feature", "This folder does not contain config.json")
                .ask(e.project!!);
        } else {
            UploadSmartActorsFeatureDialog(e.project!!, feature, getActionPath(e)).showAndGet()
        }
    }

    private fun getConfigJson(e: AnActionEvent): JsonFile? {
        return (e.dataContext.getData("psi.Element") as PsiDirectory).files.find {
            it.name == "config.json"
        } as JsonFile?
    }

    private fun getActionPath(e: AnActionEvent): String {
        val directory = e.dataContext.getData("psi.Element") as PsiDirectory
        return directory.virtualFile.path
    }
}
