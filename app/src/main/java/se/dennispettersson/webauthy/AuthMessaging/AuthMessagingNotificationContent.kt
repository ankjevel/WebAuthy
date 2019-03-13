package se.dennispettersson.webauthy.AuthMessaging

import java.util.ArrayList
import java.util.HashMap

object AuthMessagingNotificationContent {
    val ITEMS: MutableList<AuthMessagingNotification> = ArrayList()
    val ITEM_MAP: MutableMap<String, AuthMessagingNotification> = HashMap()

    fun addItem(item: AuthMessagingNotification) {
        ITEMS.add(item)
        ITEM_MAP.put(item.uuid, item)
    }
}