package com.example.noctaleapp.ui // Hoặc package của bạn

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.noctaleapp.R
import com.example.noctaleapp.adapter.ChapterAdapter
import com.example.noctaleapp.viewmodel.BookViewModel
import com.example.noctaleapp.viewmodel.BookViewModelFactory
import com.example.noctaleapp.repository.BookRepository

class ReaderPanelActivity : AppCompatActivity() {

    private lateinit var recyclerViewPanelChapters: RecyclerView
    private lateinit var chapterAdapter: ChapterAdapter
    private lateinit var textViewPanelTitle: TextView // Nếu bạn có tiêu đề

    // ViewModel để lấy danh sách chương.
    // Bạn có thể tái sử dụng BookViewModel nếu nó có chức năng tải danh sách chương theo bookId
    // hoặc tạo một ViewModel mới chuyên cho việc này.
    private lateinit var listChaptersViewModel: BookViewModel // Đổi tên cho phù hợp nếu cần

    private var currentBookIdFromIntent: String? = null
    private var currentChapterIdFromIntent: String? = null // Để highlight chương hiện tại (tùy chọn)

    companion object {
        const val EXTRA_BOOK_ID = "com.example.noctaleapp.ui.READER_PANEL_BOOK_ID"
        const val EXTRA_CURRENT_CHAPTER_ID = "com.example.noctaleapp.ui.READER_PANEL_CURRENT_CHAPTER_ID"
        const val RESULT_SELECTED_CHAPTER_ID = "com.example.noctaleapp.ui.RESULT_SELECTED_CHAPTER_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.panel_chapters_reader)

        currentBookIdFromIntent = intent.getStringExtra(EXTRA_BOOK_ID)
        currentChapterIdFromIntent = intent.getStringExtra(EXTRA_CURRENT_CHAPTER_ID)

        if (currentBookIdFromIntent.isNullOrBlank()) {
            Toast.makeText(this, "Book ID không hợp lệ.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        initViews()
        setupViewModel() // Khởi tạo và observe ViewModel
        setupRecyclerView()

        // Yêu cầu ViewModel tải danh sách chương
        listChaptersViewModel.loadChaptersForBook(currentBookIdFromIntent!!)
    }

    private fun initViews() {
        textViewPanelTitle = findViewById(R.id.textViewPanelTitle) // Nếu có
        recyclerViewPanelChapters = findViewById(R.id.recyclerViewPanelChapters)
        // Bạn có thể đặt tên sách cho panel title
        // textViewPanelTitle.text = "Chương của Sách XYZ" (lấy tên sách nếu có)
    }

    private fun setupViewModel() {
        // Sử dụng BookRepository và BookViewModelFactory như trong BookActivity hoặc ChapterActivity
        // Nếu bạn muốn một ViewModel riêng, hãy tạo ViewModel và Factory tương ứng
        val repository = BookRepository() // Hoặc nguồn repository của bạn
        val factory = BookViewModelFactory(repository) // Hoặc factory tương ứng
        listChaptersViewModel = ViewModelProvider(this, factory)[BookViewModel::class.java]

        listChaptersViewModel.chapters.observe(this) { chapters ->
            if (chapters.isNotEmpty()) {
                chapterAdapter.submitList(chapters)
                // Tùy chọn: Cuộn đến chương hiện tại nếu nó được truyền vào
                currentChapterIdFromIntent?.let { currentId ->
                    val position = chapters.indexOfFirst { it.id == currentId }
                    if (position != -1) {
                        recyclerViewPanelChapters.scrollToPosition(position)
                    }
                }
            } else {
                // Xử lý trường hợp không có chương hoặc đang tải mà danh sách rỗng
                // Toast.makeText(this, "Không có chương nào hoặc đang tải...", Toast.LENGTH_SHORT).show()
            }
        }

        listChaptersViewModel.isLoadingChapters.observe(this) { isLoading ->
            // TODO: Hiển thị/ẩn ProgressBar nếu bạn thêm nó vào panel_chapters_reader.xml
            // progressBarPanel.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        listChaptersViewModel.error.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                // Có thể finish() nếu lỗi nghiêm trọng
            }
        }
    }

    private fun setupRecyclerView() {
        chapterAdapter = ChapterAdapter { selectedChapter ->
            // Khi một chương được chọn:
            // 1. Tạo một Intent để chứa ID chương được chọn
            val resultIntent = Intent()
            resultIntent.putExtra(RESULT_SELECTED_CHAPTER_ID, selectedChapter.id)
            // 2. Đặt kết quả là RESULT_OK và gửi kèm Intent
            setResult(RESULT_OK, resultIntent)
            // 3. Đóng ReaderPanelActivity
            finish()
        }
        // Nếu bạn muốn highlight chương hiện tại trong adapter, bạn cần truyền currentChapterIdFromIntent
        // chapterAdapter.setCurrentChapterId(currentChapterIdFromIntent) // (Cần thêm hàm này vào adapter)

        recyclerViewPanelChapters.apply {
            layoutManager = LinearLayoutManager(this@ReaderPanelActivity)
            adapter = chapterAdapter
        }
    }
}