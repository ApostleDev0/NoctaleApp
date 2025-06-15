package com.example.noctaleapp.repository

import android.util.Log
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

    fun getIdByGenreName(genreName: String,
        onSuccess: (Genre) -> Unit,
        onFailure: (Exception) -> Unit) {
        genresCollection.whereEqualTo("name", genreName)
            .get()
            .addOnSuccessListener { result ->
                val genre = result.documents.firstNotNullOfOrNull { document ->
                    document.toObject(Genre::class.java)
                }
                if (genre != null) {
                    onSuccess(genre)
                    Log.d("GenreRepository", "Genre found: $genre")
                } else {
                    Log.d("GenreRepository", "Genre not found")
                }
            }
            .addOnFailureListener { exception -> onFailure(exception) }
    }
}