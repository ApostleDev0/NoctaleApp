package com.example.noctaleapp.model

data class Book (
    val title: String,
    val author: String,
    val rating: Float,
    val numberChapter: Int,
    val description: String? = null,
    val imageUrl: String? = null,
    val localImageRes: Int? = null
)