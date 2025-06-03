package com.example.noctaleapp.repository

import com.example.noctaleapp.model.Book
import com.example.noctaleapp.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.rpc.QuotaFailure

class HomeRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val booksCollection = firestore.collection("books")
    private val usersCollection = firestore.collection("users")

    fun getUserById(userId: String,
                    onSuccess: (User) -> Unit,
                    onFailure: (Exception) -> Unit) {
        usersCollection.document(userId)
            .get()
            .addOnSuccessListener {
                result ->
                val user = result.toObject(User::class.java)
                if (user != null) {
                    onSuccess(user.copy(id = result.id))
                } else {
                    onFailure(Exception("User not found"))
                }
            }
            .addOnFailureListener {
                exception ->
                onFailure(exception)
            }
    }

    fun getBookById(bookId: String,
                    onSuccess: (Book) -> Unit,
                    onFailure: (Exception) -> Unit) {
        booksCollection.document(bookId)
            .get()
            .addOnSuccessListener {
                result ->
                val book = result.toObject(Book::class.java)
                if (book != null) {
                    onSuccess(book.copy(id = result.id))
                } else {
                    onFailure(Exception("Book not found"))
                }
            }
            .addOnFailureListener {
                exception ->
                onFailure(exception)
            }
    }

    fun getRecentBookIdFromUser(userId: String,
                                onSuccess: (String) -> Unit,
                                onFailure: (Exception) -> Unit) {
        usersCollection.document(userId)
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