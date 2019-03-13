package se.dennispettersson.webauthy

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*
import se.dennispettersson.webauthy.AuthMessaging.AuthMessagingNotification
import se.dennispettersson.webauthy.AuthMessaging.AuthMessagingNotificationContent
import se.dennispettersson.webauthy.AuthMessaging.AuthMessagingNotificationRecyclerViewAdapter
import se.dennispettersson.webauthy.AuthMessaging.AuthMessagingService
import java.io.File
import java.io.FileNotFoundException

class MainActivity : AppCompatActivity() {
    private val mNotificationTag: String by lazy {
        getString(R.string.action_tag)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "init")
        readFromBundle(savedInstanceState)
        Log.d(TAG, "initalized")

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

        handleIntent(intent)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        Log.d(TAG, "save")
        outState?.run {
            putString("_AuthMessagingNotification", AuthMessagingNotificationContent.toSaveState())

            val file = File(baseContext.filesDir, "state.p")
            val parcel = Parcel.obtain()

            writeToParcel(parcel, 0)
            file.writeBytes(parcel.marshall())

            parcel.recycle()
        }
        Log.d(TAG, "saved")

        super.onSaveInstanceState(outState)
    }

    private fun handleIntent(intent: Intent) {
        val accepted = AuthMessagingService.acceptedTags.map {
            if (it != null) getString(it) else null
        }

        if (intent.action !in accepted) {
            return
        }

        val message = AuthMessagingNotification.fromIntent(intent)

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

    private fun readFromBundle(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            savedInstanceState.getString("_AuthMessagingNotification").let {
                AuthMessagingNotificationContent.fromSavedState(it as String)
            }
            return
        }

        try {
            val file = File(baseContext.filesDir, "state.p")
            val bytes = file.readBytes()

            if (bytes.isEmpty()) {
                return
            }

            val bundle = Bundle()
            val parcel = Parcel.obtain()

            parcel.unmarshall(bytes, 0, bytes.size)
            parcel.setDataPosition(0)

            bundle.readFromParcel(parcel)

            parcel.recycle()

            if (bundle.isEmpty) {
                return
            }

            bundle.getString("_AuthMessagingNotification").let {
                AuthMessagingNotificationContent.fromSavedState(it as String)
            }
        } catch (ex: FileNotFoundException) {
        }
    }

    private companion object {
        const val TOPIC_NAME = "auth"
        const val TAG = "MA"

        var mAuthMessagingNotificationRecyclerViewAdapter: AuthMessagingNotificationRecyclerViewAdapter? = null
    }
}
