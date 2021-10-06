package info.smart_tools.smartactors.devtools.builder.task

import com.intellij.notification.Notification
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import info.smart_tools.smartactors.devtools.builder.tool.buildFeature

enum class BuilderNotificationGroup(val id: String, val type: NotificationType) {
    SUCCESS("SmartActors.Notification.BuildFeature.Success", NotificationType.INFORMATION),
    FAIL("SmartActors.Notification.BuildFeature.Fail", NotificationType.ERROR),
}

data class BuilderNotificationParams(
    val properties: BuilderNotificationGroup,
    val content: String
)

fun buildNotification(params: BuilderNotificationParams): Notification {
    return NotificationGroupManager.getInstance().getNotificationGroup(params.properties.id)
        .createNotification(content = params.content, params.properties.type)
        .setTitle("SmartActors")
}

fun build(featurePath: String): BuilderNotificationParams {
    val result = buildFeature(featurePath);
    return if (result) {
        BuilderNotificationParams(BuilderNotificationGroup.SUCCESS, "Feature has been built")
    } else {
        BuilderNotificationParams(BuilderNotificationGroup.FAIL, "Failed to build feature")
    }
}

class BuildFeatureTask(
    private val ideaProject: Project?,
    private val featurePath: String
) : Task.Backgroundable(ideaProject, "Building feature...") {
    override fun run(indicator: ProgressIndicator) {
        indicator.isIndeterminate = false

        val notificationParams = build(featurePath)
        buildNotification(notificationParams).notify(ideaProject)
    }
}
