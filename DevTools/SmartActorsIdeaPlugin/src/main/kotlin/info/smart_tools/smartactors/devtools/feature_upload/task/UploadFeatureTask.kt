package info.smart_tools.smartactors.devtools.feature_upload.task

import com.intellij.notification.NotificationType
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import info.smart_tools.smartactors.devtools.common.feature.FeatureName
import info.smart_tools.smartactors.devtools.common.feature.Repository
import info.smart_tools.smartactors.devtools.common.model.Credentials
import info.smart_tools.smartactors.devtools.common.notification.NotificationConfig
import info.smart_tools.smartactors.devtools.common.notification.buildNotification
import info.smart_tools.smartactors.devtools.feature_upload.tool.upload

class UploadFeatureTask(
    private val ideaProject: Project?,
    private val featureName: FeatureName,
    private val credentials: Credentials,
    private val repository: Repository,
    private val path: String
) : Task.Backgroundable(ideaProject, "Uploading feature $featureName...") {

    override fun run(indicator: ProgressIndicator) {
        indicator.isIndeterminate = true

        val notification = uploadFeature()
        buildNotification(notification).notify(ideaProject)
    }

    private fun uploadFeature(): NotificationConfig {
        val result = upload(path, credentials, repository)

        return if (result) {
            NotificationConfig(
                id = "SmartActors.Notification.UploadFeature.Success",
                type = NotificationType.INFORMATION,
                content = "Feature $featureName was uploaded to ${repository.url}."
            )
        } else {
            NotificationConfig(
                id = "SmartActors.Notification.UploadFeature.Fail",
                type = NotificationType.ERROR,
                content = "Failed to upload $featureName to ${repository.url}."
            )
        }
    }
}
