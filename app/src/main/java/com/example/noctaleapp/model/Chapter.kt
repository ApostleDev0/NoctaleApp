package com.example.noctaleapp.model

data class Chapter(
    val id: String = "",
    val mainTitle: String = "",
    val subTitle: String = "",
    val content: String = "",
    val bookID: String = "",
    val chapterNumber: Int = 0,
    val previousChapterId: String? = null, // Thêm dòng này
    val nextChapterId: String? = null
)
