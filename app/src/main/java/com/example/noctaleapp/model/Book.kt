package com.example.noctaleapp.model

import com.google.firebase.Timestamp

data class Book(
    val id: String = "",
    val title: String = "",
    val author: String = "",
    val genres: List<String> = listOf(),
    val description: String? = null,
    val coverUrl: String = "",
    val uploadedAt: Timestamp? = null,
    val updatedAt: Timestamp? = null,
    val rating: Double = 0.0,
    val chapterQuantity: Int = 0
)