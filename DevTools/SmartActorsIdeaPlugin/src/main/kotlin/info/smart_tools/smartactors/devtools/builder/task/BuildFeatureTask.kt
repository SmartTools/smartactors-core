package info.smart_tools.smartactors.devtools.builder.task

import com.intellij.notification.NotificationType
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import info.smart_tools.smartactors.devtools.builder.tool.buildFeature
import info.smart_tools.smartactors.devtools.common.notification.NotificationConfig
import info.smart_tools.smartactors.devtools.common.notification.buildNotification

class BuildFeatureTask(
    private val ideaProject: Project?,
    private val featurePath: String
) : Task.Backgroundable(ideaProject, "Building feature...") {
    override fun run(indicator: ProgressIndicator) {
        indicator.isIndeterminate = false

        val notificationParams = build(featurePath)
        buildNotification(notificationParams).notify(ideaProject)
    }

    private fun build(featurePath: String): NotificationConfig {
        val result = buildFeature(featurePath);
        return if (result) {
            NotificationConfig(
                "SmartActors.Notification.BuildFeature.Success",
                NotificationType.INFORMATION,
                "Feature has been built"
            )
        } else {
            NotificationConfig(
                "SmartActors.Notification.BuildFeature.Fail",
                NotificationType.ERROR,
                "Failed to build feature"
            )
        }
    }
}
