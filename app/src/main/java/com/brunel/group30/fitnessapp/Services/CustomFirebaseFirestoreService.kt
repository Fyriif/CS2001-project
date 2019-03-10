package com.brunel.group30.fitnessapp.Services

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

object CustomFirebaseFirestoreService {
    fun getDocument(collectionName: String, documentName: String): Task<DocumentSnapshot> {
        val firestore = FirebaseFirestore.getInstance()

        return firestore.collection(collectionName).document(documentName)
                .get()
    }

    fun <T> sendDocument(collectionName: String, documentName: String, objectToStore: T): Task<Void> {
        val firestore = FirebaseFirestore.getInstance()

        return firestore.collection(collectionName).document(documentName)
                .set(objectToStore!!)
    }
}