package com.example.noctaleapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.noctaleapp.model.Book
import com.example.noctaleapp.model.Chapter
import com.example.noctaleapp.repository.BookRepository
import kotlinx.coroutines.launch

class BookViewModel(private val repository: BookRepository) : ViewModel() {

    private val _bookDetails = MutableLiveData<Book?>()
    val bookDetails: LiveData<Book?> = _bookDetails

    private val _chapters = MutableLiveData<List<Chapter>>()
    val chapters: LiveData<List<Chapter>> = _chapters

    private val _isLoadingBook = MutableLiveData<Boolean>() // <-- Mới
    val isLoadingBook: LiveData<Boolean> = _isLoadingBook

    private val _isLoadingChapters = MutableLiveData<Boolean>()
    val isLoadingChapters: LiveData<Boolean> = _isLoadingChapters

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadBookAndChapterDetails(bookId: String) {
        if (bookId.isBlank()) {
            _error.value = "Book ID không hợp lệ."
            _bookDetails.value = null
            _chapters.value = emptyList()
            _isLoadingBook.value = false
            _isLoadingChapters.value = false
            return
        }

        _bookDetails.value = null
        _chapters.value = emptyList()
        _error.value = null
        _isLoadingBook.value = true // Bắt đầu tải sách
        _isLoadingChapters.value = false // Chưa tải chương

        loadBookDetails(bookId)
    }

    fun loadBookDetails(id: String) {
        viewModelScope.launch {
            _isLoadingBook.postValue(true)
            try {
                val book = repository.getBookByIdSuspend(id)
                _bookDetails.postValue(book)
                _isLoadingBook.postValue(false) // Tải sách xong
                if (book != null) { // Chỉ tải chương nếu sách tồn tại
                    loadChaptersForBook(id) // Hoặc book.id
                } else {
                    // Nếu book là null (ví dụ repository trả về null thay vì ném lỗi)
                    _error.postValue("Không tìm thấy thông tin sách.")
                    _isLoadingChapters.postValue(false) // Không tải chương
                    _chapters.postValue(emptyList())
                }

            } catch (e: BookRepository.BookNotFoundException) {
                _error.postValue("Không tìm thấy sách: ${e.message}")
                _bookDetails.postValue(null)
                _isLoadingBook.postValue(false)
                _isLoadingChapters.value = false
                _chapters.postValue(emptyList())
            } catch (e: Exception) { // Bắt các Exception khác
                _error.postValue("Lỗi tải thông tin sách: ${e.message}")
                _bookDetails.postValue(null)
                _isLoadingBook.postValue(false)
                _isLoadingChapters.value = false
                _chapters.postValue(emptyList())
            } finally {
            }
        }
    }

    fun loadChaptersForBook(bookId: String) {
        viewModelScope.launch {
            _isLoadingChapters.value = true
            try {
                val chapterList = repository.getChaptersForBookSuspend(bookId)
                _chapters.postValue(chapterList)
            } catch (e: Exception) {
                _error.postValue("Lỗi khi tải danh sách chương: ${e.message}")
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

@Suppress("UNCHECKED_CAST")
class BookViewModelFactory(private val repository: BookRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookViewModel::class.java)) {
            return BookViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}