package com.brunel.group30.fitnessapp.Utils


import android.util.Log

object Exceptions {
    object FirestoreExceptions {
        private val TAG = "FIRESTORE"

        @JvmStatic
        fun errorFailedToGetDocument(collectionName: String, documentName: String) {
            logError(
                    TAG,
                    "Failed to load $collectionName/$documentName from Firebase Firestore!"
            )
        }

        @JvmStatic
        fun errorDocumentNonExisting(collectionName: String, documentName: String) {
            logError(
                    TAG,
                    "$collectionName/$documentName doesn't seem to exit in Firebase Firestore!"
            )
        }

        @JvmStatic
        fun errorFailedToWriteDocument(collectionName: String, documentName: String) {
            logError(
                    TAG,
                    "Failed to write document $collectionName/$documentName into Firebase Firestore!"
            )
        }
    }

    private fun logError(tag: String, message: String) {
        Log.e(tag, message)
    }
}
