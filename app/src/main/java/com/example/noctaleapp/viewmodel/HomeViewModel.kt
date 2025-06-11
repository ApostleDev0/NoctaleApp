package com.example.noctaleapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.noctaleapp.model.Book
import com.example.noctaleapp.model.User
import com.example.noctaleapp.repository.HomeRepository

class HomeViewModel : ViewModel() {
    private val repository = HomeRepository()

    private val _user = MutableLiveData<User>()
    val users: LiveData<User> = _user

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _book = MutableLiveData<Book>()
    val books: LiveData<Book> = _book

    init {

    }

    fun fetchUserById(userId: String) {
        repository.getUserById(userId,
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
        repository.getRecentBookIdFromUser(userId,
            onSuccess = {
                bookId ->
                repository.getBookById(
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
            },)

    }
}

//i hope i can build that, i try so hard i get sofa
//Chưa nghĩ được gì