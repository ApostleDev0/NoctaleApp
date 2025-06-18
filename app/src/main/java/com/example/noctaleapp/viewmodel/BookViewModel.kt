package com.example.noctaleapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.noctaleapp.model.Book
import com.example.noctaleapp.model.Chapter
import com.example.noctaleapp.model.Genre // Thêm import cho Genre
import com.example.noctaleapp.repository.BookRepository
import com.example.noctaleapp.repository.GenreRepository // Thêm import cho GenreRepository
import kotlinx.coroutines.launch

class BookViewModel(
    private val bookRepository: BookRepository,
    private val genreRepository: GenreRepository // Thêm GenreRepository vào constructor
) : ViewModel() {

    private val _bookDetails = MutableLiveData<Book?>()
    val bookDetails: LiveData<Book?> = _bookDetails

    private val _chapters = MutableLiveData<List<Chapter>>()
    val chapters: LiveData<List<Chapter>> = _chapters

    // LiveData cho Genres
    private val _allGenres = MutableLiveData<List<Genre>>()
    val allGenres: LiveData<List<Genre>> = _allGenres

    private val _isLoadingBook = MutableLiveData<Boolean>()
    val isLoadingBook: LiveData<Boolean> = _isLoadingBook

    private val _isLoadingChapters = MutableLiveData<Boolean>()
    val isLoadingChapters: LiveData<Boolean> = _isLoadingChapters

    // LiveData cho trạng thái tải Genres
    private val _isLoadingGenres = MutableLiveData<Boolean>()
    val isLoadingGenres: LiveData<Boolean> = _isLoadingGenres

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        // Tải danh sách tất cả thể loại ngay khi ViewModel được khởi tạo
        loadAllGenresInternal()
    }

    private fun loadAllGenresInternal() {
        _isLoadingGenres.value = true
        genreRepository.getAllGenres( // Sử dụng hàm từ GenreRepository
            onSuccess = { genres ->
                _allGenres.value = genres
                _isLoadingGenres.value = false
                Log.d("BookViewModel", "Genres loaded: ${genres.size} items")
            },
            onFailure = { exception ->
                _error.value = "Lỗi tải danh sách thể loại: ${exception.message}"
                _isLoadingGenres.value = false
                _allGenres.value = emptyList() // Hoặc giữ giá trị cũ/null tùy logic
                Log.e("BookViewModel", "Failed to load genres", exception)
            }
        )
    }

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
        _error.value = null // Reset lỗi
        _isLoadingBook.value = true
        _isLoadingChapters.value = false // Chưa tải chương cho đến khi sách tải xong

        loadBookDetails(bookId)
    }

    fun loadBookDetails(id: String) {
        viewModelScope.launch { // Đã có viewModelScope nên không cần postValue cho isLoadingBook nữa
            _isLoadingBook.value = true
            try {
                val book = bookRepository.getBookByIdSuspend(id) // Giả định hàm suspend
                _bookDetails.value = book
                if (book != null) {
                    // Nếu allGenres chưa tải xong, có thể đợi ở Activity hoặc xử lý hiển thị tạm
                    // Ở đây, chúng ta chỉ cần book.id để tải chương
                    loadChaptersForBook(book.id) // Sử dụng book.id để đảm bảo tính nhất quán
                } else {
                    _error.value = "Không tìm thấy thông tin sách với ID: $id"
                    _chapters.value = emptyList()
                    _isLoadingChapters.value = false
                }
            } catch (e: BookRepository.BookNotFoundException) {
                _error.value = "Không tìm thấy sách: ${e.message}"
                _bookDetails.value = null // Đảm bảo bookDetails là null khi không tìm thấy
                _chapters.value = emptyList()
                _isLoadingChapters.value = false
            } catch (e: Exception) {
                _error.value = "Lỗi tải thông tin sách: ${e.message}"
                _bookDetails.value = null
                _chapters.value = emptyList()
                _isLoadingChapters.value = false
                Log.e("BookViewModel", "Error loading book details", e)
            } finally {
                _isLoadingBook.value = false // Luôn đặt lại isLoadingBook trong finally
            }
        }
    }

    fun loadChaptersForBook(bookId: String) {
        viewModelScope.launch {
            _isLoadingChapters.value = true
            try {
                val chapterList = bookRepository.getChaptersForBookSuspend(bookId)
                _chapters.value = chapterList
            } catch (e: Exception) {
                _error.value = "Lỗi khi tải danh sách chương: ${e.message}"
                _chapters.value = emptyList()
                Log.e("BookViewModel", "Error loading chapters for book: $bookId", e)
            } finally {
                _isLoadingChapters.value = false
            }
        }
    }

    // Hàm helper để Activity lấy danh sách tên thể loại
    fun getGenreNamesForCurrentBook(): List<String> {
        val currentBook = _bookDetails.value ?: return emptyList()
        val loadedGenres = _allGenres.value ?: return emptyList()

        if (loadedGenres.isEmpty() && currentBook.genres.isNotEmpty()) {
            Log.w("BookViewModel", "Trying to get genre names, but allGenres is empty. Genres might still be loading.")
            // Có thể trả về danh sách ID nếu muốn hiển thị tạm gì đó
            // return currentBook.genres.map { "ID: $it" }
        }

        return currentBook.genres.mapNotNull { genreId ->
            loadedGenres.find { it.id == genreId }?.name
        }
    }

    fun clearErrorMessage() {
        _error.value = null
    }
}

// Cập nhật BookViewModelFactory
@Suppress("UNCHECKED_CAST")
class BookViewModelFactory(
    private val bookRepository: BookRepository,
    private val genreRepository: GenreRepository // Thêm GenreRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookViewModel::class.java)) {
            return BookViewModel(bookRepository, genreRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}