package com.example.noctaleapp.repository

import android.util.Log
import com.example.noctaleapp.model.User
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    fun getUserById(uid: String,
                    onSuccess: (User) -> Unit,
                    onFailure: (Exception) -> Unit) {
        usersCollection.document(uid)
            .get()
            .addOnSuccessListener {
                    result ->
                val user = result.toObject(User::class.java)
                if (user != null) {
                    onSuccess(user.copy(uid = result.id))
                } else {
                    onFailure(Exception("User not found"))
                }
            }
            .addOnFailureListener {
                    exception ->
                Log.e("FirestoreError", "Failed to fetch user", exception)
                onFailure(exception)
            }
    }

    fun getRecentBookIdFromUser(uid: String,
                                onSuccess: (String) -> Unit,
                                onFailure: (Exception) -> Unit) {
        usersCollection.document(uid)
            .get()
            .addOnSuccessListener {
                    result ->
                val user = result.toObject(User::class.java)
                if (user != null) {
                    val bookId = user.recentReading?.get("bookId") as? String
                    if (bookId != null) {
                        onSuccess(bookId)
                    } else {
                        onFailure(Exception("Recent book ID not found"))
                    }
                } else {
                    onFailure(Exception("User not found"))
                }
            }
            .addOnFailureListener {
                    exception ->
                onFailure(exception)
            }
    }
}