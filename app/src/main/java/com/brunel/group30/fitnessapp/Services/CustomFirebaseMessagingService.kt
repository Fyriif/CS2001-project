package com.brunel.group30.fitnessapp.Services

import android.content.Context
import android.widget.Toast
import com.appizona.yehiahd.fastsave.FastSave
import com.brunel.group30.fitnessapp.Models.NotificationToken
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import java.util.*


class CustomFirebaseMessagingService : FirebaseMessagingService() {
    /**
     * If you want to send messages to this application instance or
     * manage this apps subscriptions on the server side, send the
     * Instance ID token to your app server.
     */
    override fun onNewToken(token: String) {
        sendRegistrationToServer(token, applicationContext)
    }

    companion object {
        @JvmStatic
        fun isNewTokenRequired(applicationContext: Context) {
            FastSave.init(applicationContext)

            val notificationToken = FastSave.getInstance().getObject(
                    NotificationToken.PREF_KEY_NAME, NotificationToken::class.java)
            if (notificationToken == null || notificationToken.timeZone != TimeZone.getDefault().id) {
                // Re-generate the token
                FirebaseMessaging.getInstance().isAutoInitEnabled = true
                FirebaseInstanceId.getInstance().instanceId
                        .addOnCompleteListener(OnCompleteListener { task ->
                            if (!task.isSuccessful) {
                                return@OnCompleteListener
                            }

                            sendRegistrationToServer(task.result?.token.toString(),
                                    applicationContext)
                        })
            }
        }

        private fun sendRegistrationToServer(token: String, applicationContext: Context) {
            FastSave.init(applicationContext)

            val notificationToken = NotificationToken(token, TimeZone.getDefault().id)
            FastSave.getInstance().saveObject(NotificationToken.PREF_KEY_NAME, notificationToken)

            FirebaseFirestore.getInstance().collection(NotificationToken.COLLECTION_NAME)
                    .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                    .set(notificationToken)
                    .addOnFailureListener {
                        Toast.makeText(applicationContext, "Error writing document",
                                Toast.LENGTH_LONG).show()
                    }
        }
    }
}