package com.brunel.group30.fitnessapp.Models

import com.brunel.group30.fitnessapp.Services.CustomFirebaseFirestoreService
import com.brunel.group30.fitnessapp.Utils.Exceptions
import com.google.firebase.firestore.PropertyName

data class Goals(@PropertyName("steps_target") var stepsTarget: Int = 10000) {
    companion object {
        const val COLLECTION_NAME = "goals"
        const val PREF_KEY_NAME = "goals"
    }

    fun updateDB(userUid: String) {
        val sendUserGoalsTask = CustomFirebaseFirestoreService.sendDocument(
                Goals.COLLECTION_NAME,
                userUid,
                this
        )

        sendUserGoalsTask.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Exceptions.FirestoreExceptions.errorFailedToWriteDocument(
                        Goals.COLLECTION_NAME,
                        userUid
                )
            }
        }
    }
}