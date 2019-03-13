package se.dennispettersson.webauthy.AuthMessaging

import android.support.v4.util.ArraySet
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

object AuthMessagingNotificationContent {
    val ITEMS: MutableList<AuthMessagingNotification> = ArrayList()
    val ITEM_MAP: MutableMap<String, AuthMessagingNotification> = HashMap()

    private val TAG = "AMNC"

    fun addItem(item: AuthMessagingNotification) {
        Log.d(TAG, "addItem")

        if (ITEM_MAP.containsKey(item.uuid)) {
            Log.d(TAG, "contained Key")
            return
        }

        ITEMS.add(item)
        ITEM_MAP.put(item.uuid, item)

        ITEMS.map { Log.d(TAG, "contained key ${it}")}
    }

    fun toSaveState(): String {
        Log.d(TAG, "toSave")

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
        Log.d(TAG, "fromSavedState")

        ITEMS.clear()
        ITEM_MAP.clear()

        val array = JSONArray(state)
        for (i in 0..(array.length() - 1)) {
            addItem(AuthMessagingNotification.fromJSONObject(array.get(i) as JSONObject))
        }
    }
}