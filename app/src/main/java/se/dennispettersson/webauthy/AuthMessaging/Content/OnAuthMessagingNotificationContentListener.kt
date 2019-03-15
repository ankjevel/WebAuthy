package se.dennispettersson.webauthy.AuthMessaging.Content

interface OnAuthMessagingNotificationContentListener {
    fun onUpdate(item: AuthMessagingNotification?, index: Int?)
}