package se.dennispettersson.webauthy.AuthMessaging.Content

import android.content.Intent
import org.json.JSONObject

data class AuthMessagingNotification(
    val ip: String,
    val org: String,
    val city: String,
    val region: String,
    val base: String,
    val allow: String,
    val deny: String,
    val status: String,
    val uuid: String
) {
    override fun toString(): String = "${toMap()}"

    fun getURL(input: String): String {
        return "$base/${if (input == "allow") allow else deny}"
    }

    fun toMap(): Map<String, String> = hashMapOf(
        "ip" to ip,
        "org" to org,
        "city" to city,
        "region" to region,
        "base" to base,
        "allow" to allow,
        "deny" to deny,
        "status" to status,
        "uuid" to uuid
    )

    companion object {
        val KEYS = hashSetOf(
            "ip", "org", "city", "region", "base", "allow", "deny", "status", "uuid"
        )

        // private val TAG = "AMN"
        operator fun invoke(map: Map<String, String>): AuthMessagingNotification =
            AuthMessagingNotification(
                map["ip"] as String,
                map["org"] as String,
                map["city"] as String,
                map["region"] as String,
                map["base"] as String,
                map["allow"] as String,
                map["deny"] as String,
                map["status"] as String,
                map["uuid"] as String
            )

        fun fromIntent(intent: Intent): AuthMessagingNotification {
            val hashMap = KEYS.map { it to intent.getStringExtra(it) }.toMap()
            return invoke(hashMap)
        }

        fun fromData(data: Map<String, String>): AuthMessagingNotification {
            val hashMap = KEYS.map { it to data.getOrDefault(it, "") }.toMap()
            return invoke(hashMap)
        }

        fun fromJSONObject(data: JSONObject): AuthMessagingNotification {
            val hashMap = KEYS.map { it to data.getString(it) }.toMap()
            return invoke(hashMap)
        }
    }
}
