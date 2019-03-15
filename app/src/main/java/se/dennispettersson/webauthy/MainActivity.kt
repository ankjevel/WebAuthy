package se.dennispettersson.webauthy

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*
import se.dennispettersson.webauthy.AuthMessaging.AuthMessagingNotificationRecyclerViewAdapter
import se.dennispettersson.webauthy.AuthMessaging.AuthMessagingService
import se.dennispettersson.webauthy.AuthMessaging.Content.AuthMessagingNotification
import se.dennispettersson.webauthy.AuthMessaging.Content.AuthMessagingNotificationContent
import se.dennispettersson.webauthy.AuthMessaging.Content.OnAuthMessagingNotificationContentListener
import se.dennispettersson.webauthy.AuthMessaging.OnListFragmentInteractionListener

class MainActivity : AppCompatActivity() {
    private val mNotificationTag: String by lazy {
        getString(R.string.action_tag)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SaveState.readFromBundle(baseContext, savedInstanceState)

        FirebaseApp.initializeApp(this)

        setContentView(R.layout.activity_main)

        recyclerList.adapter = AuthMessagingNotificationRecyclerViewAdapter(
            AuthMessagingNotificationContent.ITEMS,
            object : OnListFragmentInteractionListener {
                override fun onListFragmentInteraction(item: AuthMessagingNotification?) {
                    Log.d(TAG, "click! ${AuthMessagingNotificationContent.ITEMS.indexOf(item)} ${item}")
                    AuthMessagingNotificationContent.removeItem(item)
                }
            }
        )

        ItemTouchHelper(SwipeController().apply {
            addActions(object : SwipeControllerActions {
                override fun onSwipeLeft(position: Int) {
                    Log.d(TAG, "onSwipeLeft, DENY ${getItem(position)}")
                    AuthMessagingNotificationContent.removeItem(getItem(position))
                }

                override fun onSwipeRight(position: Int) {
                    Log.d(TAG, "onSwipeRight, ALLOW ${getItem(position)}")
                    AuthMessagingNotificationContent.removeItem(getItem(position))
                }

                private fun getItem(position: Int): AuthMessagingNotification? {
                    return AuthMessagingNotificationContent.ITEMS.reversed().get(position)
                }
            })
        }).apply {
            attachToRecyclerView(recyclerList)
        }

        AuthMessagingNotificationContent.addListener(
            object : OnAuthMessagingNotificationContentListener {
                override fun onUpdate(item: AuthMessagingNotification?, index: Int?) {
                    Thread(Runnable {
                        this@MainActivity.runOnUiThread {
                            recyclerList.adapter?.notifyDataSetChanged()
                        }
                    }).start()
                }
            }
        )

        FirebaseMessaging
            .getInstance()
            .subscribeToTopic(TOPIC_NAME)

        handleIntent(intent)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(
            SaveState.onSaveInstanceState(baseContext, outState)
        )
    }

    private fun handleIntent(intent: Intent) {
        val accepted = AuthMessagingService.acceptedTags.map {
            if (it != null) getString(it) else null
        }

        if (intent.action !in accepted) {
            return
        }

        val message = AuthMessagingNotification.fromIntent(intent)

        Log.d(TAG, "handleIntent: ${message}, action: ${intent.action}")

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

    companion object {
        const val TOPIC_NAME = "auth"
        const val TAG = "MA"
    }
}
