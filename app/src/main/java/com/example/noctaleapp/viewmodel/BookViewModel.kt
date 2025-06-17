package com.example.noctaleapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noctaleapp.model.Book
import com.example.noctaleapp.model.Chapter
import com.example.noctaleapp.repository.BookRepository
import kotlinx.coroutines.launch

class BookViewModel : ViewModel() {

    private val repository = BookRepository()

    private val _bookDetails = MutableLiveData<Book?>()
    val bookDetails: LiveData<Book?> = _bookDetails

    private val _chapters = MutableLiveData<List<Chapter>>()
    val chapters: LiveData<List<Chapter>> = _chapters

    private val _isLoadingChapters = MutableLiveData<Boolean>()
    val isLoadingChapters: LiveData<Boolean> = _isLoadingChapters

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadBookAndChapterDetails(bookId: String) {
        if (bookId.isBlank()) {
            _error.value = "Book ID không hợp lệ."
            _bookDetails.value = null
            _chapters.value = emptyList()
            return
        }

        _bookDetails.value = null
        _chapters.value = emptyList()
        _error.value = null

        loadBookDetails(bookId)
    }

    private fun loadBookDetails(id: String) {
        viewModelScope.launch {
            try {
                val book = repository.getBookByIdSuspend(id)
                _bookDetails.postValue(book)
                loadChaptersForBook(id)

            } catch (e: BookRepository.BookNotFoundException) {
                _error.postValue("Không tìm thấy sách: ${e.message}")
                _bookDetails.postValue(null)
                _isLoadingChapters.value = false
                _chapters.postValue(emptyList())
            } catch (e: Exception) { // Bắt các Exception khác
                _error.postValue("Lỗi tải thông tin sách: ${e.message}")
                _bookDetails.postValue(null)
                _isLoadingChapters.value = false
                _chapters.postValue(emptyList())
            } finally {
            }
        }
    }

    private fun loadChaptersForBook(bookId: String) {
        viewModelScope.launch {
            _isLoadingChapters.value = true
            try {
                val chapterList = repository.getChaptersForBookSuspend(bookId)
                _chapters.postValue(chapterList)
            } catch (e: Exception) {
                _chapters.postValue(emptyList())
            } finally {
                _isLoadingChapters.value = false
            }
        }
    }

    fun clearErrorMessage() {
        _error.value = null
    }
}