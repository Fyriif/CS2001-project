package com.brunel.group30.fitnessapp.Services

import android.content.Context
import com.appizona.yehiahd.fastsave.FastSave
import com.brunel.group30.fitnessapp.Models.NotificationToken
import com.brunel.group30.fitnessapp.Models.UserInfo
import com.brunel.group30.fitnessapp.Utils.Exceptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
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

            val userUid: String = FirebaseAuth.getInstance().currentUser?.uid.toString();
            val task: Task<Void> = CustomFirebaseFirestoreService.sendDocument(
                    NotificationToken.COLLECTION_NAME,
                    userUid,
                    notificationToken
            )

            task.addOnFailureListener {
                Exceptions.FirestoreExceptions.errorFailedToWriteDocument(
                        UserInfo.COLLECTION_NAME,
                        userUid
                )
            }
        }
    }
}