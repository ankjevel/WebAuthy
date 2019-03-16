package se.dennispettersson.webauthy.AuthMessaging.Content

import android.content.Context
import android.util.ArraySet
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import se.dennispettersson.webauthy.MainActivity
import se.dennispettersson.webauthy.SaveState

object AuthMessagingNotificationContent {
    private val TAG = "AMNC"
    private val listeners: MutableSet<OnAuthMessagingNotificationContentListener> = ArraySet()

    val ITEMS: MutableSet<AuthMessagingNotification> = ArraySet()
    private val keys: MutableSet<String> = ArraySet()

    private fun notifyListenersUpdate(
        item: AuthMessagingNotification? = null,
        index: Int? = null
    ) {
        listeners.forEach { listener ->
            listener.onUpdate(item, index)
        }
    }

    fun addItem(item: AuthMessagingNotification, ignoreUpdate: Boolean? = false) {
        if (keys.contains(item.uuid)) {
            return
        }

        ITEMS.add(item)
        keys.add(item.uuid)

        if (ignoreUpdate == true) {
            return
        }

        SaveState.saveInstanceState()

        notifyListenersUpdate(item)
    }

    fun removeItem(item: AuthMessagingNotification?) {
        Log.d(TAG, "pre_removed ${item}")

        if (item == null) {
            return
        }

        ITEMS.remove(item)
        keys.remove(item.uuid)

        Log.d(TAG, "removed")

        SaveState.saveInstanceState()

        notifyListenersUpdate(item, ITEMS.indexOf(item))
    }

    fun addListener(listener: OnAuthMessagingNotificationContentListener) {
        listeners.add(listener)
    }

    fun toSaveState(): String {
        if (ITEMS.isEmpty()) {
            return "[]"
        }

        val jsonArray = JSONArray()

        for (item in ITEMS) {
            val jsonObject = JSONObject()

            for ((key, value) in item.toMap()) {
                jsonObject.put(key, value)
            }

            jsonArray.put(jsonObject)
        }

        return jsonArray.toString()
    }

    fun fromSavedState(state: String) {
        ITEMS.clear()
        keys.clear()

        val array = JSONArray(state)
        for (i in 0..(array.length() - 1)) {
            addItem(
                AuthMessagingNotification.fromJSONObject(
                    array.get(i) as JSONObject
                ),
                ignoreUpdate = true
            )
        }

        notifyListenersUpdate()
    }
}