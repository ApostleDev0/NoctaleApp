package com.example.noctaleapp.repository

import com.example.noctaleapp.model.Genre
import com.google.firebase.firestore.FirebaseFirestore

class GenreRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val genresCollection = firestore.collection("genres")

    fun getAllGenres(onSuccess: (List<Genre>) -> Unit,
                     onFailure: (Exception) -> Unit) {
        genresCollection.get()
            .addOnSuccessListener { result ->
                val genres = result.documents.mapNotNull { document ->
                    document.toObject(Genre::class.java)
                }
                onSuccess(genres)
            }
            .addOnFailureListener { exception -> onFailure(exception) }
    }
}