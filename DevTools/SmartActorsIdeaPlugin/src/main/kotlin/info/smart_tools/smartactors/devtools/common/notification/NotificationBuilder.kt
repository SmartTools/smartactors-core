package info.smart_tools.smartactors.devtools.common.notification

import com.intellij.notification.Notification
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType

data class NotificationConfig(
    val id: String,
    val type: NotificationType,
    val content: String
)

fun buildNotification(config: NotificationConfig): Notification {
    return NotificationGroupManager.getInstance().getNotificationGroup(config.id)
        .createNotification(content = config.content, config.type)
        .setTitle("SmartActors")
}
