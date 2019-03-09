package com.brunel.group30.fitnessapp.Models

import com.google.firebase.firestore.PropertyName

data class NotificationToken(@get:PropertyName("token") @set:PropertyName("token") var token: String,
                             @get:PropertyName("time_zone") @set:PropertyName("time_zone") var timeZone: String) {
    companion object {
        const val COLLECTION_NAME = "notification-tokens"
        const val PREF_KEY_NAME = "notification_token"
    }
}