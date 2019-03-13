package se.dennispettersson.webauthy.AuthMessaging

import android.content.Intent
import org.json.JSONObject

data class AuthMessagingNotification(
    val uuid: String,
    val ip: String,
    val base: String
) {
    override fun toString(): String = "${toMap()}"

    fun toMap(): Map<String, String> = KEYS.map { it to it }.toMap()

    companion object {
        val KEYS = hashSetOf("uuid", "ip", "base")

        private val TAG = "AMN"

        operator fun invoke(map: Map<String, String>): AuthMessagingNotification =
            AuthMessagingNotification(
                map["uuid"] as String,
                map["ip"] as String,
                map["base"] as String
            )

        fun fromIntent(intent: Intent): AuthMessagingNotification {
            val hashMap = KEYS.map { it to intent.getStringExtra(it) }.toMap()
            return AuthMessagingNotification.invoke(hashMap)
        }

        fun fromData(data: Map<String, String>): AuthMessagingNotification {
            val hashMap = KEYS.map { it to data.getOrDefault(it, "") }.toMap()
            return AuthMessagingNotification.invoke(hashMap)
        }

        fun fromJSONObject(data: JSONObject): AuthMessagingNotification {
            val hashMap = KEYS.map { it to data.getString(it) }.toMap()
            return AuthMessagingNotification.invoke(hashMap)
        }
    }
}
