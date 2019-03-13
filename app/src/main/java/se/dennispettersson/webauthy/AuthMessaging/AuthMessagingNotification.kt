package se.dennispettersson.webauthy.AuthMessaging

import android.content.Intent
import org.json.JSONObject

object AuthMessagingNotificationFactory {
    fun fromIntent(intent: Intent): AuthMessagingNotification {
        val ip = intent.getStringExtra("ip")
        val uuid = intent.getStringExtra("uuid")
        val base = intent.getStringExtra("base")

        return AuthMessagingNotification(uuid, ip, base)
    }

    fun fromData(data: Map<String, String>): AuthMessagingNotification {
        val ip = data.getOrDefault("ip", "")
        val uuid = data.getOrDefault("uuid", "")
        val base = data.getOrDefault("base", "")

        return AuthMessagingNotification(uuid, ip, base)
    }

    fun fromJSONObject(data: JSONObject): AuthMessagingNotification {
        val ip = data.getString("ip")
        val uuid = data.getString("uuid")
        val base = data.getString("base")

        return AuthMessagingNotification(uuid, ip, base)
    }
}

data class AuthMessagingNotification(
    val uuid: String,
    val ip: String,
    val base: String
) {
    override fun toString(): String = "[${uuid}, ${ip}, ${base}]"
}
