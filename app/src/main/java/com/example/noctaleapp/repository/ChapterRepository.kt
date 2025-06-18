package com.example.noctaleapp.repository

import android.util.Log
import com.example.noctaleapp.model.Chapter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ChapterRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val chaptersCollection = "chapters"
    private val booksCollection = "books"

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
    suspend fun getChapterDetailsSuspend(bookId: String, chapterId: String): Chapter? {
        return try {
            val documentSnapshot = firestore.collection(booksCollection).document(bookId)
                .collection(chaptersCollection).document(chapterId)
                .get()
                .await()

            val chapter = documentSnapshot.toObject(Chapter::class.java)
            chapter?.copy(id = documentSnapshot.id)
        } catch (e: Exception) {
            Log.e("ChapterRepository", "Error getting chapter details for $chapterId in book $bookId", e)
            null
        }
    }

    suspend fun getChapterByNumberSuspend(bookId: String, chapterNumber: Int): Chapter? {
        return try {
            val querySnapshot = firestore.collection(booksCollection).document(bookId)
                .collection(chaptersCollection)
                .whereEqualTo("chapterNumber", chapterNumber)
                .limit(1)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val document = querySnapshot.documents.first()
                val chapter = document.toObject(Chapter::class.java)
                chapter?.copy(id = document.id)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("ChapterRepository", "Error getting chapter by number $chapterNumber for book $bookId", e)
            null
        }
    }

    suspend fun getPreviousChapterIdSuspend(bookId: String, currentChapterNumber: Int): String? {
        if (currentChapterNumber <= 1) return null

        return try {
            val querySnapshot = firestore.collection(booksCollection).document(bookId)
                .collection(chaptersCollection)
                .whereEqualTo("chapterNumber", currentChapterNumber - 1)
                .limit(1)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                querySnapshot.documents.first().id
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("ChapterRepository", "Error getting previous chapter ID for book $bookId, current number $currentChapterNumber", e)
            null
        }
    }


    suspend fun getNextChapterIdSuspend(bookId: String, currentChapterNumber: Int): String? {
        return try {
            val querySnapshot = firestore.collection(booksCollection).document(bookId)
                .collection(chaptersCollection)
                .whereEqualTo("chapterNumber", currentChapterNumber + 1)
                .limit(1)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                querySnapshot.documents.first().id
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("ChapterRepository", "Error getting next chapter ID for book $bookId, current number $currentChapterNumber", e)
            null
        }
    }
}