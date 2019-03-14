package se.dennispettersson.webauthy.AuthMessaging.Content

import org.json.JSONArray
import org.json.JSONObject
import java.util.*

object AuthMessagingNotificationContent {
    val ITEMS: MutableList<AuthMessagingNotification> = ArrayList()
    val ITEM_MAP: MutableMap<String, AuthMessagingNotification> = HashMap()

    private val TAG = "AMNC"

    fun addItem(item: AuthMessagingNotification) {
        if (ITEM_MAP.containsKey(item.uuid)) {
            return
        }

        ITEMS.add(item)
        ITEM_MAP.put(item.uuid, item)
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
    }
}