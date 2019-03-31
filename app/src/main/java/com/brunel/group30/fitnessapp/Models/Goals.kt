package com.brunel.group30.fitnessapp.Models

import com.brunel.group30.fitnessapp.Services.CustomFirebaseFirestoreService
import com.brunel.group30.fitnessapp.Utils.Exceptions
import com.google.firebase.firestore.PropertyName

data class Goals(@get:PropertyName("steps_target") @set:PropertyName("steps_target") var stepsTarget: Int = 10000,
                 @get:PropertyName("weight_target") @set:PropertyName("weight_target") var weightTarget: Int = 60,
                 @get:PropertyName("hydration_target") @set:PropertyName("hydration_target") var hydrationTarget: Int = 3000,
                 @get:PropertyName("calorie_target") @set:PropertyName("calorie_target") var calorieTarget: Int = 100) {

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