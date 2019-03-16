package se.dennispettersson.webauthy.AuthMessaging

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Icon
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import se.dennispettersson.webauthy.AuthMessaging.Content.AuthMessagingNotification
import se.dennispettersson.webauthy.AuthMessaging.Content.AuthMessagingNotificationContent
import se.dennispettersson.webauthy.MainActivity
import se.dennispettersson.webauthy.R
import java.util.*

internal class AuthMessagingService : FirebaseMessagingService() {
    init {
        createChannel()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        if (
            remoteMessage == null ||
            remoteMessage.data == null ||
            remoteMessage.data?.isEmpty() == true
        ) {
            Log.d(TAG, "empty message? $remoteMessage")
            return
        }

        val authMessagingNotification = AuthMessagingNotification.fromData(
            remoteMessage.data
        )

        AuthMessagingNotificationContent.addItem(authMessagingNotification)

        showNotification(authMessagingNotification)
    }

    private fun genIntent(intentAction: String?, data: AuthMessagingNotification): PendingIntent {
        val intent = Intent(MainActivity.instance, MainActivity::class.java).apply {
            action = intentAction
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("notification_id", mNotificationId)
            for ((key, value) in data.toMap()) {
                putExtra(key, value)
            }
        }

        val stackBuilder = TaskStackBuilder.create(this).apply {
            addParentStack(MainActivity::class.java)
            addNextIntent(intent)
        }

        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun genAction(
        label: Int,
        action: Int,
        data: AuthMessagingNotification
    ): Notification.Action {
        return Notification.Action
            .Builder(
                Icon.createWithResource(
                    this,
                    android.R.drawable.ic_dialog_info
                ),
                getString(label),
                genIntent(
                    intentAction = getString(action),
                    data = data
                )
            )
            .build()
    }

    private fun createChannel() {
        val manager by lazy {
            val context = MainActivity.instance as Context
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }

        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableVibration(true)
                setShowBadge(true)
                enableLights(true)
                lightColor = Color.GREEN
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
        )
    }

    private fun showNotification(
        data: AuthMessagingNotification
    ) {
        val body = getString(R.string.msg_body, data.ip)

        val notification = Notification
            .Builder(
                applicationContext,
                CHANNEL_ID
            )
            .apply {
                setContentIntent(
                    genIntent(
                        intentAction = null,
                        data = data
                    )
                )
                setAutoCancel(true)
                setContentTitle(getString(R.string.msg_title))
                setSmallIcon(android.R.drawable.ic_dialog_info)
                setChannelId(CHANNEL_ID)
                setContentText(body)
                setActions(
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
                setGroup(CHANNEL_GROUP)
                style = Notification.BigTextStyle().bigText(body)
            }
            .build()
            .apply {
                flags = Notification.FLAG_AUTO_CANCEL
            }

        getSystemService(Context.NOTIFICATION_SERVICE)?.let {
            val manager = it as NotificationManager

            manager.notify(
                getString(R.string.action_tag),
                mNotificationId,
                notification
            )
        }
    }

    companion object {
        const val TAG = "AMS"
        const val CHANNEL_ID = "se.dennispettersson.webauthy.channel.notification"
        const val CHANNEL_NAME = "New notification"
        const val CHANNEL_GROUP = "new_notification"

        val mNotificationId = Random().nextInt(1024) + (1024 * 2)
        val acceptedTags = arrayListOf(
            R.string.action_allow,
            R.string.action_deny,
            null
        )
    }
}
