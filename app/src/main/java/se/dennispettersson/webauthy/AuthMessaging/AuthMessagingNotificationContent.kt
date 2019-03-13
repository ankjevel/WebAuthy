package se.dennispettersson.webauthy.AuthMessaging

import org.json.JSONArray
import org.json.JSONObject
import java.util.*

object AuthMessagingNotificationContent {
    val ITEMS: MutableList<AuthMessagingNotification> = ArrayList()
    val ITEM_MAP: MutableMap<String, AuthMessagingNotification> = HashMap()

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

            jsonObject.put("uuid", item.uuid)
            jsonObject.put("ip", item.ip)
            jsonObject.put("base", item.base)

            jsonArray.put(jsonObject)
        }

        return jsonArray.toString()
    }

    fun fromSavedState(state: String) {
        ITEMS.clear()
        ITEM_MAP.clear()

        val array = JSONArray(state)
        val length = array.length()

        if (length > 1) {
            for (i in 0..length) {
                addItem(AuthMessagingNotificationFactory.fromJSONObject(array.get(i) as JSONObject))
            }
        } else if (length == 1) {
            addItem(AuthMessagingNotificationFactory.fromJSONObject(array.get(0) as JSONObject))
        }
    }
}