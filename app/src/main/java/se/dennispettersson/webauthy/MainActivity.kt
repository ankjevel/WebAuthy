package se.dennispettersson.webauthy

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*
import se.dennispettersson.webauthy.AuthMessaging.AuthMessagingNotificationContent
import se.dennispettersson.webauthy.AuthMessaging.AuthMessagingNotificationFactory
import se.dennispettersson.webauthy.AuthMessaging.AuthMessagingNotificationRecyclerViewAdapter
import se.dennispettersson.webauthy.AuthMessaging.AuthMessagingService

class MainActivity : AppCompatActivity() {

    private val mNotificationTag: String by lazy {
        getString(R.string.action_tag)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        mAuthMessagingNotificationRecyclerViewAdapter =
            AuthMessagingNotificationRecyclerViewAdapter(
                AuthMessagingNotificationContent.ITEMS,
                null
            )

        recyclerList.adapter = mAuthMessagingNotificationRecyclerViewAdapter

        FirebaseMessaging
            .getInstance()
            .subscribeToTopic(TOPIC_NAME)
            .addOnCompleteListener { task ->
                val msg = getString(if (task.isSuccessful()) R.string.msg_subscribed else R.string.msg_subscribe_failed)
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            }

        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        val accepted = AuthMessagingService.acceptedTags.map {
            if (it != null) getString(it) else null
        }

        if (intent.action !in accepted) {
            return
        }

        val message = AuthMessagingNotificationFactory.fromIntent(intent)

        Log.d(TAG, "${message}, action: ${intent.action}")

        if (intent.action == null) {
            return
        }

        getSystemService(Context.NOTIFICATION_SERVICE)?.let {
            val manager = it as NotificationManager
            for (notification in manager.activeNotifications) {
                if (notification.tag != mNotificationTag) {
                    continue
                }

                manager.cancel(notification.tag, notification.id)
            }
        }
    }

    private companion object {
        const val TOPIC_NAME = "auth"
        const val TAG = "MA"
        var mAuthMessagingNotificationRecyclerViewAdapter: AuthMessagingNotificationRecyclerViewAdapter? = null
    }
}
