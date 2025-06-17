package com.example.noctaleapp.repository

import android.util.Log
import com.example.noctaleapp.model.Book
import com.example.noctaleapp.model.Chapter
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class BookRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val booksCollection = firestore.collection("books")
    private val chaptersCollection = firestore.collection("chapters")

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
                Log.d("BookRepository", "Error: ${exception.message}")
            }
    }

    fun getBookByGenreId(genreId: String,
                       onSuccess: (List<Book>) -> Unit,
                       onFailure: (Exception) -> Unit) {
        booksCollection.whereArrayContains("genres", genreId)
            .get()
            .addOnSuccessListener {
                result ->
                val books = result.documents.mapNotNull {
                    it.toObject(Book::class.java)?.copy(id = it.id)
                }
                onSuccess(books)
            }
            .addOnFailureListener {
                exception ->
                onFailure(exception)
            }
    }

    fun getSuggestBook(limited: Long,
                       lastVisible: DocumentSnapshot?,
                       onSuccess: (List<Book>, DocumentSnapshot?) -> Unit,
                       onFailure: (Exception) -> Unit) {
        var query = booksCollection.orderBy("title")
            .limit(limited)

        if (lastVisible != null) {
            query = query.startAfter(lastVisible)
        }

        query.get().addOnSuccessListener {
            result ->
            val books = result.documents.mapNotNull {
                it.toObject(Book::class.java)?.copy(id = it.id)
            }

            val newLastVisible = result.documents.lastOrNull()
            onSuccess(books, newLastVisible)
        }
            .addOnFailureListener {
                exception ->
                onFailure(exception)
            }
    }

    suspend fun getBookByIdSuspend(bookId: String): Book {
        try {
            val documentSnapshot = booksCollection.document(bookId).get().await()
            val book = documentSnapshot.toObject(Book::class.java)
            if (book != null) {
                return book.copy(id = documentSnapshot.id)
            } else {
                throw BookNotFoundException("Book with ID '$bookId' not found or could not be deserialized.")
            }
        } catch (e: Exception) {
            if (e is BookNotFoundException) throw e
            throw Exception("Failed to fetch book with ID '$bookId'. Cause: ${e.message}", e)
        }
    }


    suspend fun getChaptersForBookSuspend(bookId: String): List<Chapter> {
        try {
            val querySnapshot = firestore.collection("books").document(bookId)
                .collection("chapters") // TRUY VẤN SUB-COLLECTION
                .orderBy("chapterNumber")
                .get()
                .await()
            return querySnapshot.documents.mapNotNull { documentSnapshot ->
                documentSnapshot.toObject(Chapter::class.java)?.copy(id = documentSnapshot.id)
            }
        } catch (e: Exception) {
            throw Exception("Failed to fetch chapters for book ID '$bookId'. Cause: ${e.message}", e)
        }
    }

    class BookNotFoundException(message: String) : Exception(message)
}