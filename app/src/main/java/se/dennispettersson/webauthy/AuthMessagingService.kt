package se.dennispettersson.webauthy

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Icon
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*

internal class AuthMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        if (
            remoteMessage == null ||
            remoteMessage.data == null ||
            remoteMessage.data?.isEmpty() == true
        ) {
            return
        }

        val data = remoteMessage.data

        Log.d(TAG, "Message data payload: \"${data}\"")

        showNotification(
            hashMapOf(
                "uuid" to data.getOrDefault("uuid", ""),
                "ip" to data.getOrDefault("ip", "0.0.0.0"),
                "base" to data.getOrDefault("base", "http://base")
            )
        )
}

    private val mNotificationManager: NotificationManager by lazy {
        val context = this.applicationContext
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private fun genIntent(intentAction: String?, data: Map<String, String>): PendingIntent {
        val stackBuilder = TaskStackBuilder.create(this)

        stackBuilder.addParentStack(MainActivity::class.java)
        stackBuilder.addNextIntent(Intent(this, MainActivity::class.java).apply {
            action = intentAction

            putExtra("notification_id", mNotificationId)
            putExtra("ip", data.getOrDefault("ip", ""))
            putExtra("uuid", data.getOrDefault("uuid", ""))
            putExtra("base", data.getOrDefault("base", ""))
        })

        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT)
    }

    private fun genAction(
        label: Int,
        action: Int,
        data: Map<String, String>
    ): Notification.Action {
        return Notification.Action
            .Builder(
                Icon.createWithResource(this, android.R.drawable.ic_dialog_info),
                getString(label),
                genIntent(
                    intentAction = getString(action),
                    data = data
                )
            )
            .build()
    }

    private fun createChannel() {
        NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).let { channel ->
            channel.enableVibration(true)
            channel.setShowBadge(true)
            channel.enableLights(true)
            channel.lightColor = Color.GREEN
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

            mNotificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(
        data: Map<String, String>
    ) {
        createChannel()

        val body = getString(R.string.msg_body, data.get("ip"))

        val notification = Notification.Builder(applicationContext, CHANNEL_ID)
            .setContentIntent(
                genIntent(
                    intentAction = null,
                    data = data
                )
            )
            .setAutoCancel(true)
            .setContentTitle(getString(R.string.msg_title))
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setStyle(
                Notification.BigTextStyle().bigText(body)
            )
            .setChannelId(CHANNEL_ID)
            .setContentText(body)
            .setActions(
                genAction(
                    label = R.string.msg_allow,
                    action = R.string.action_allow,
                    data = data
                ),
                genAction(
                    label = R.string.msg_deny,
                    action = R.string.action_deny,
                    data = data
                )
            )
            .setGroup(CHANNEL_GROUP)
            .build()

        notification.flags = Notification.FLAG_AUTO_CANCEL

        getSystemService(Context.NOTIFICATION_SERVICE)?.let {
            (it as NotificationManager).notify(
                getString(R.string.action_tag),
                mNotificationId,
                notification
            )
        }
    }

    private companion object {
        const val TAG = "AMS"
        const val CHANNEL_ID = "se.dennispettersson.webauthy.channel.notification"
        const val CHANNEL_NAME = "New notification"
        const val CHANNEL_GROUP = "new_notification"

        val mNotificationId = Random().nextInt(1024) + (1024 * 2)
    }
}
