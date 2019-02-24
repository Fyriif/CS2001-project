package com.brunel.group30.fitnessapp.Models

import com.google.firebase.firestore.PropertyName

class NotificationToken(@PropertyName("token") var token: String) {
    companion object {
        val COLLECTION_NAME = "notification-tokens"
        val PREF_KEY_NAME = "notification_token"
    }
}