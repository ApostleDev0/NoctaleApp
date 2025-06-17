package com.example.noctaleapp.model

data class User(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val avatarUrl: String = "",
    val recentReading: Map<String, Any> = emptyMap(),
    val phoneNumber: String = "",
    val followers: List<String> = listOf(),
    val following: List<String> = listOf(),
    val libraryBook: List<String> = listOf(),
    val username: String = "",
)
