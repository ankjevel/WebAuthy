package se.dennispettersson.webauthy

interface AuthMessagingNotificationImpl {
    val uuid: String
    val ip: String
    val base: String
}

class AuthMessagingNotification(data: Map<String, String>) : AuthMessagingNotificationImpl {
    override val uuid = data.getOrDefault("uuid", "")
    override val ip = data.getOrDefault("ip", "")
    override val base = data.getOrDefault("base", "")
}