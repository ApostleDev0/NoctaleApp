package com.example.noctaleapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.example.noctaleapp.model.Book
import com.example.noctaleapp.model.Genre
import com.example.noctaleapp.model.RecentBook
import com.example.noctaleapp.model.User
import com.example.noctaleapp.repository.BookRepository
import com.example.noctaleapp.repository.GenreRepository
import com.example.noctaleapp.repository.UserRepository

class HomeViewModel : ViewModel() {
    private val userRepository = UserRepository()
    private val bookRepository = BookRepository()
    private val genreRepository = GenreRepository()

    private val _user = MutableLiveData<User>()
    val users: LiveData<User> = _user

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _book = MutableLiveData<Book>()
    val books: LiveData<Book> = _book

    private val _genres = MutableLiveData<List<Genre>>()
    val genres: LiveData<List<Genre>> = _genres

    fun fetchUserById(userId: String) {
        userRepository.getUserById(userId,
            onSuccess = {
                user ->
                _user.value = user
                Log.d("UserViewModel", "User loaded: $user")
                Log.d("BookName", "Recent reading: ${user.recentReading}")
            },
            onFailure = {
                exception ->
                _error.value = exception.message
            }
        )
    }

    fun fetchRecentBookByUser(userId: String) {
        userRepository.getRecentBookIdFromUser(userId,
            onSuccess = {
                bookId ->
                bookRepository.getBookById(
                    bookId,
                    onSuccess = {
                        bookData ->
                        _book.value = bookData
                        Log.d("BookViewModel", "Book loaded: ${bookData.title}")
                    },
                    onFailure = {
                        exception -> _error.value = exception.message
                    }
                )
            },
            onFailure = {
                exception ->
                _error.value = exception.message
            }
        )
    }

    val recentBookData: LiveData<RecentBook> = _book.map {
        book ->
        RecentBook(
            id = book.id,
            title = book.title,
            author = book.author,
            imageUrl = book.coverUrl,
            progressRead = 0
        )
    }

    fun fetchGenres() {
        genreRepository.getAllGenres(
            onSuccess = {
                genresData ->
                _genres.value = genresData
            },
            onFailure = {
                exception ->
                _error.value = exception.message
            }
        )
    }
}