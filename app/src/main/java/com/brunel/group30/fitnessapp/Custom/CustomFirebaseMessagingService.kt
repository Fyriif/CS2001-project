package com.brunel.group30.fitnessapp.Custom

import com.appizona.yehiahd.fastsave.FastSave
import com.brunel.group30.fitnessapp.Models.NotificationToken
import com.google.firebase.messaging.FirebaseMessagingService

class CustomFirebaseMessagingService : FirebaseMessagingService() {
    /**
     * If you want to send messages to this application instance or
     * manage this apps subscriptions on the server side, send the
     * Instance ID token to your app server.
     */
    override fun onNewToken(token: String) {
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {
        FastSave.init(applicationContext)
        FastSave.getInstance().saveString(NotificationToken.PREF_KEY_NAME, token)
    }
}