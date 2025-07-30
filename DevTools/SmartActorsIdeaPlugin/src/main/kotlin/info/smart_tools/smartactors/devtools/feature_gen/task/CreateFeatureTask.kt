package info.smart_tools.smartactors.devtools.feature_gen.task

import com.intellij.notification.NotificationType
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import info.smart_tools.smartactors.devtools.common.feature.FeatureName
import info.smart_tools.smartactors.devtools.common.notification.NotificationConfig
import info.smart_tools.smartactors.devtools.common.notification.buildNotification
import info.smart_tools.smartactors.devtools.feature_gen.tool.createFeature
import okhttp3.internal.notify

class CreateFeatureTask(
    private val ideaProject: Project?,
    private val featureName: FeatureName,
    private val path: String
) : Task.Modal(ideaProject, "Creating Feature", false) {

    override fun run(indicator: ProgressIndicator) {
        indicator.text = "Creating feature $featureName"
        indicator.isIndeterminate = true

        val notification = create()
        buildNotification(notification).notify(ideaProject)
    }

    private fun create(): NotificationConfig {
        val result = createFeature(
            groupId = featureName.groupId,
            featureName = featureName.name,
            version = featureName.version,
            path
        )

        return if (result) {
            NotificationConfig(
                id = "SmartActors.Notification.CreateFeature.Success",
                type = NotificationType.INFORMATION,
                content = "Feature $featureName was created."
            )
        } else {
            NotificationConfig(
                id = "SmartActors.Notification.CreateFeature.Fail",
                type = NotificationType.ERROR,
                content = "Failed to create feature $featureName"
            )
        }
    }
}
