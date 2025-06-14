package com.example.noctaleapp.repository

import com.example.noctaleapp.model.Book
import com.google.firebase.firestore.FirebaseFirestore

class BookRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val booksCollection = firestore.collection("books")

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
}