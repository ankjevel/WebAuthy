package se.dennispettersson.webauthy

import android.content.Context
import android.os.Bundle
import android.os.Parcel
import se.dennispettersson.webauthy.AuthMessaging.Content.AuthMessagingNotificationContent
import java.io.File
import java.io.FileNotFoundException

object SaveState {
    private val context by lazy {
        MainActivity.instance as Context
    }

    fun saveInstanceState(
        outState: Bundle? = Bundle()
    ): Bundle? {
        outState?.run {
            putString("_AuthMessagingNotification", AuthMessagingNotificationContent.toSaveState())

            val file = File(context.filesDir, "state.p")
            val parcel = Parcel.obtain()

            writeToParcel(parcel, 0)
            file.writeBytes(parcel.marshall())

            parcel.recycle()
        }

        return outState
    }

    fun readFromBundle(
        context: Context,
        savedInstanceState: Bundle?
    ) {
        if (savedInstanceState != null) {
            savedInstanceState.getString("_AuthMessagingNotification").let {
                AuthMessagingNotificationContent.fromSavedState(it as String)
            }

            return
        }

        try {
            val file = File(context.filesDir, "state.p")
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
}