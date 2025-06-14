package com.example.noctaleapp.repository

import com.example.noctaleapp.model.Chapter
import com.google.firebase.firestore.FirebaseFirestore

class ChapterRepository {
    private val firestore = FirebaseFirestore.getInstance()

    fun getChapterById(bookId: String, chapterId: String,
                       onSuccess: (Chapter) -> Unit,
                       onFailure: (Exception) -> Unit) {
        firestore.collection("books").document(bookId)
            .collection("chapters").document(chapterId)
            .get()
            .addOnSuccessListener {
                result ->
                val chapter = result.toObject(Chapter::class.java)
                if (chapter != null) {
                    onSuccess(chapter.copy(id = result.id))
                } else {
                    onFailure(Exception("Chapter not found"))
                }
            }
            .addOnFailureListener {
                exception ->
                onFailure(exception)
            }
    }

    fun getChapterByNumber(bookId: String, chapterNumber: Int,
                           onSuccess: (Chapter) -> Unit,
                           onFailure: (Exception) -> Unit) {
        firestore.collection("books").document(bookId)
            .collection("chapters")
            .whereEqualTo("number", chapterNumber).limit(1)
            .get()
            .addOnSuccessListener {
                result ->
                val documents = result.documents.firstOrNull()
                val chapter = documents?.toObject(Chapter::class.java)
                if (documents != null && chapter != null) {
                    onSuccess(chapter.copy(id = documents.id))
                } else {
                    onFailure(Exception("Chapter not found"))
                }
            }
            .addOnFailureListener {
                exception ->
                onFailure(exception)
            }
    }
}