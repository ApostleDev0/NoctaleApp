package com.example.noctaleapp.model

data class User(
    val id: String = "",
    val displayName: String = "",
    val email: String = "",
    val avatarUrl: String = "",
    val recentReading: Map<String, Any> = emptyMap(),
    val phone: String = "",
    val followers: List<String> = listOf(),
    val following: List<String> = listOf(),
    val libraryBook: List<String> = listOf(),
    val description: String = "",
)
