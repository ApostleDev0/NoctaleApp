package com.example.noctaleapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.noctaleapp.model.Chapter // Đảm bảo model này có id và chapterNumber
import com.example.noctaleapp.repository.ChapterRepository
import kotlinx.coroutines.launch

// ViewModelFactory để khởi tạo ChapterViewModel với ChapterRepository
class ChapterViewModelFactory(
    private val chapterRepository: ChapterRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChapterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChapterViewModel(chapterRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

class ChapterViewModel(
    private val chapterRepo: ChapterRepository // Dependency là ChapterRepository
) : ViewModel() {

    private val _chapterDetails = MutableLiveData<Chapter?>()
    val chapterDetails: LiveData<Chapter?> = _chapterDetails

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _previousChapterId = MutableLiveData<String?>()
    val previousChapterId: LiveData<String?> = _previousChapterId

    private val _nextChapterId = MutableLiveData<String?>()
    val nextChapterId: LiveData<String?> = _nextChapterId

    /**
     * Tải thông tin chi tiết của một chương cụ thể, cũng như ID của chương trước và sau.
     * @param bookId ID của cuốn sách.
     * @param chapterId ID của chương cần tải.
     */
    fun loadChapter(bookId: String, chapterId: String) {
        if (bookId.isBlank() || chapterId.isBlank()) {
            _error.value = "Book ID hoặc Chapter ID không hợp lệ."
            _chapterDetails.value = null
            _previousChapterId.value = null
            _nextChapterId.value = null
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val chapter = chapterRepo.getChapterDetailsSuspend(bookId, chapterId)
                _chapterDetails.postValue(chapter)

                if (chapter != null) {
                    if (chapter.chapterNumber > 0) {
                        _previousChapterId.postValue(
                            chapterRepo.getPreviousChapterIdSuspend(bookId, chapter.chapterNumber)
                        )
                        _nextChapterId.postValue(
                            chapterRepo.getNextChapterIdSuspend(bookId, chapter.chapterNumber)
                        )
                    } else {
                        _previousChapterId.postValue(null)
                        _nextChapterId.postValue(null)
                    }
                } else {
                    _previousChapterId.postValue(null)
                    _nextChapterId.postValue(null)
                    _error.postValue("Không thể tải nội dung chương.")
                }

            } catch (e: Exception) {
                _chapterDetails.postValue(null)
                _previousChapterId.postValue(null)
                _nextChapterId.postValue(null)
                _error.postValue("Lỗi khi tải chương: ${e.localizedMessage ?: "Lỗi không xác định"}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearErrorMessage() {
        _error.value = null
    }
}