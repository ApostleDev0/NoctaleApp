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
import com.example.noctaleapp.repository.AuthRepository
import com.example.noctaleapp.repository.BookRepository
import com.example.noctaleapp.repository.GenreRepository
import com.example.noctaleapp.repository.UserRepository
import com.google.firebase.firestore.DocumentSnapshot

class HomeViewModel : ViewModel() {
    private val userRepository = UserRepository()
    private val bookRepository = BookRepository()
    private val genreRepository = GenreRepository()
    private val authRepository = AuthRepository()

    private val _user = MutableLiveData<User>()
    val users: LiveData<User> = _user

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _book = MutableLiveData<Book>()
    val books: LiveData<Book> = _book

    private val _bookGenre = MutableLiveData<List<Book>>()
    val bookGenres: LiveData<List<Book>> = _bookGenre

    private val _genres = MutableLiveData<List<Genre>>()
    val genres: LiveData<List<Genre>> = _genres

    private var isLoadingSuggest = false
    private var isLastSuggestPage = false
    private var lastSuggestSnapshot: DocumentSnapshot? = null
    private val _suggestBook = MutableLiveData<List<Book>>(emptyList())
    val suggestBooks: LiveData<List<Book>> = _suggestBook
    private val pageSize = 10L

    private val _logoutComplete = MutableLiveData<Boolean>(false)
    val logoutComplete: LiveData<Boolean> = _logoutComplete

    private val _uid = MutableLiveData<String>()
    val uid: LiveData<String> get() = _uid

    init {
        fetchGenres()
    }

    fun setUID (uid: String) {
        _uid.value = uid
    }

    fun fetchUserById(uid: String) {
        userRepository.getUserById(uid,
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

    fun fetchRecentBookByUser(uid: String) {
        userRepository.getRecentBookIdFromUser(uid,
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
                        Log.d("BookViewModel", "Error: ${exception.message}")
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

    fun fetchBooksByGenre(genreName: String) {
        genreRepository.getIdByGenreName(genreName,
            onSuccess = {
                genreData ->
                Log.d("GenreViewModel", "Genre loaded: ${genreData.name}")
                bookRepository.getBookByGenreId(genreData.id,
                    onSuccess = {
                        booksData ->
                        _bookGenre.value = booksData
                    },
                    onFailure = {
                        exception ->
                    })
            },
            onFailure = {
                exception ->
                _error.value = exception.message
            }
        )
    }

    fun fetchSuggestBooks() {
        if (isLoadingSuggest || isLastSuggestPage) return
        isLoadingSuggest = true

        bookRepository.getSuggestBook(
            limited = pageSize,
            lastVisible = lastSuggestSnapshot,
            onSuccess = {newBooks, lastVisible ->
                val current = _suggestBook.value ?: emptyList()
                _suggestBook.value = current + newBooks
                Log.d("SuggestViewModel", "Suggest books loaded: ${newBooks.size}")
                isLastSuggestPage = newBooks.size < 10
                lastSuggestSnapshot = lastVisible
                isLoadingSuggest = false
            },
            onFailure = {
                isLoadingSuggest = false
            })
    }

    fun isLastSuggestPage(): Boolean = isLastSuggestPage

    fun logout() {
        authRepository.signOut()
        _logoutComplete.value = true
    }
}