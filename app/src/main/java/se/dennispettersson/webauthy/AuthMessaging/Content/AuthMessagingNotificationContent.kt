package se.dennispettersson.webauthy.AuthMessaging.Content

import android.util.ArraySet
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

object AuthMessagingNotificationContent {
    private val TAG = "AMNC"
    private val listeners: MutableSet<OnAuthMessagingNotificationContentListener> = ArraySet()

    val ITEMS: MutableSet<AuthMessagingNotification> = ArraySet()
    val ITEM_MAP: MutableMap<String, AuthMessagingNotification> = HashMap()

    private fun notifyListenersUpdate(
        item: AuthMessagingNotification? = null,
        index: Int? = null
    ) {
        listeners.forEach { listener ->
            listener.onUpdate(item, index)
        }
    }

    fun addItem(item: AuthMessagingNotification, ignoreUpdate: Boolean? = false) {
        if (ITEM_MAP.containsKey(item.uuid)) {
            return
        }

        ITEMS.add(item)
        ITEM_MAP.put(item.uuid, item)

        if (ignoreUpdate == true) {
            return
        }

        notifyListenersUpdate(item)
    }

    fun removeItem(item: AuthMessagingNotification?) {
        Log.d(TAG, "pre_removed ${item}")

        if (item == null) {
            return
        }

        val index = ITEMS.indexOf(item)

        ITEM_MAP.remove(item.uuid)
        ITEMS.remove(item)

        Log.d(TAG, "removed")

        notifyListenersUpdate(item, index)
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
        ITEM_MAP.clear()

        val array = JSONArray(state)
        for (i in 0..(array.length() - 1)) {
            addItem(
                AuthMessagingNotification.fromJSONObject(
                    array.get(i) as JSONObject
                )
            )
        }

        notifyListenersUpdate()
    }
}