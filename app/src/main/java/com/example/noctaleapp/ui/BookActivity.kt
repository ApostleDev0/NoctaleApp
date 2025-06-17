package com.example.noctaleapp.ui // Giả sử context là .ui.BookActivity từ XML

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.noctaleapp.R
import com.example.noctaleapp.adapter.ChapterAdapter
import com.example.noctaleapp.model.Book
import com.example.noctaleapp.viewmodel.BookViewModel
import android.content.Intent
import android.widget.ProgressBar
import com.example.noctaleapp.repository.BookRepository
import com.example.noctaleapp.viewmodel.BookViewModelFactory

class BookActivity : AppCompatActivity() {

    private lateinit var bookViewModel: BookViewModel
    private lateinit var imageViewBookCover: ImageView
    private lateinit var textViewBookTitle: TextView
    private lateinit var textViewBookGenre: TextView
    private lateinit var textViewBookDescription: TextView
    private lateinit var textViewChaptersLabel: TextView
    private lateinit var recyclerViewChapters: RecyclerView
    private lateinit var chapterAdapter: ChapterAdapter
    private lateinit var progressBarBookLoading: ProgressBar

    private var currentBookIdForNavigation: String? = null // BIẾN MỚI ĐỂ LƯU BOOK ID CHO NAVIGATION

    companion object {
        const val EXTRA_BOOK_ID = "extra_book_id"
        const val EXTRA_FOCUS_CHAPTERS = "com.example.noctaleapp.ui.FOCUS_CHAPTER_LIST"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book)

        initViews()
        val receivedBookId = intent.getStringExtra(EXTRA_BOOK_ID)
        if (receivedBookId.isNullOrBlank()) {
            Toast.makeText(this, "Book ID không hợp lệ.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        currentBookIdForNavigation = receivedBookId // LƯU LẠI BOOK ID

        setupRecyclerView()
        val bookRepository = BookRepository() // <--- TẠO REPOSITORY
        val viewModelFactory = BookViewModelFactory(bookRepository) // <--- TẠO FACTORY

        bookViewModel = ViewModelProvider(this, viewModelFactory)[BookViewModel::class.java] // <--- KHỞI TẠO VIEWMODEL

        val bookId = intent.getStringExtra(EXTRA_BOOK_ID)
        val shouldFocusChapters = intent.getBooleanExtra(EXTRA_FOCUS_CHAPTERS, false)

        if (shouldFocusChapters) {
            recyclerViewChapters.post {
                recyclerViewChapters.smoothScrollToPosition(0) }
        }
        if (bookId.isNullOrBlank()) {
            Toast.makeText(this, "Book ID không hợp lệ.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        observeViewModel()
        bookViewModel.loadBookAndChapterDetails(bookId)
    }

    private fun initViews() {
        progressBarBookLoading = findViewById(R.id.progressBarBookLoading)
        imageViewBookCover = findViewById(R.id.imageViewBookCover)
        textViewBookTitle = findViewById(R.id.textViewBookTitle)
        textViewBookGenre = findViewById(R.id.textViewBookGenre)
        textViewBookDescription = findViewById(R.id.textViewBookDescription)
        textViewChaptersLabel = findViewById(R.id.textViewChaptersLabel)
        recyclerViewChapters = findViewById(R.id.recyclerViewChapters)
    }

    private fun observeViewModel() {
        bookViewModel.isLoadingBook.observe(this) { isLoading ->
            if (isLoading) {
                progressBarBookLoading.visibility = View.VISIBLE
                // Bạn có thể ẩn các view thông tin sách ở đây nếu muốn
                // imageViewBookCover.visibility = View.GONE
                // textViewBookTitle.visibility = View.GONE
                // ...
            } else {
                progressBarBookLoading.visibility = View.GONE
                // Hiển thị lại các view thông tin sách nếu bạn đã ẩn chúng
                // (Mặc dù việc hiển thị chúng sẽ được xử lý khi bookDetails có dữ liệu)
            }
        }
        bookViewModel.bookDetails.observe(this) { book ->
            if (book != null) {
                displayBookInfo(book)
                textViewChaptersLabel.visibility = View.VISIBLE
                recyclerViewChapters.visibility = View.VISIBLE
                imageViewBookCover.visibility = View.VISIBLE
                textViewBookTitle.visibility = View.VISIBLE
            } else {
                imageViewBookCover.visibility = View.GONE // Hoặc đặt ảnh placeholder
                textViewBookTitle.text = getString(R.string.not_found_result)
                textViewChaptersLabel.visibility = View.GONE
                recyclerViewChapters.visibility = View.GONE
            }
        }

        bookViewModel.chapters.observe(this) { chapters ->
            if (chapters.isNotEmpty() || bookViewModel.isLoadingChapters.value == true) {
                textViewChaptersLabel.visibility = View.VISIBLE
                recyclerViewChapters.visibility = View.VISIBLE
                chapterAdapter.submitList(chapters)
            } else {
                textViewChaptersLabel.visibility = View.GONE
                recyclerViewChapters.visibility = View.GONE
            }
        }

        bookViewModel.isLoadingChapters.observe(this) { isLoading ->
            // Tương tự, bạn có thể có một ProgressBar riêng cho việc tải chapter
            // if (isLoading) { /* show chapter progress */ } else { /* hide chapter progress */ }
            // Và cập nhật visibility của recyclerViewChapters/textViewChaptersLabel
            if (isLoading || bookViewModel.chapters.value?.isNotEmpty() == true) {
                textViewChaptersLabel.visibility = View.VISIBLE
                recyclerViewChapters.visibility = View.VISIBLE
            } else if (!isLoading && bookViewModel.chapters.value.isNullOrEmpty()) {
                textViewChaptersLabel.visibility = View.GONE
                recyclerViewChapters.visibility = View.GONE
            }
        }


        bookViewModel.error.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                bookViewModel.clearErrorMessage()
            }
        }
    }

    private fun displayBookInfo(book: Book) {
        textViewBookTitle.text = book.title
        textViewBookGenre.text = if (book.genres.isNotEmpty()) "Thể loại: ${book.genres.joinToString(", ")}" else "Thể loại: Đang cập nhật"
        textViewBookDescription.text = book.description

        Glide.with(this)
            .load(book.coverUrl)
            .placeholder(R.drawable.ic_bookcover)
            .error(R.drawable.ic_bookcover)
            .into(imageViewBookCover)
    }

    private fun openChapterActivity(bookId: String, chapterId: String) {
        val intent = Intent(this, ChapterActivity::class.java).apply {
            putExtra(ChapterActivity.EXTRA_BOOK_ID, bookId) // Sử dụng const từ ChapterActivity
            putExtra(ChapterActivity.EXTRA_CHAPTER_ID, chapterId) // Sử dụng const từ ChapterActivity
        }
        startActivity(intent)
    }


    private fun setupRecyclerView() {
        chapterAdapter = ChapterAdapter { selectedChapter -> // selectedChapter ở đây là đối tượng Chapter
            // Toast.makeText(this, "Clicked (RecyclerView): ${selectedChapter.mainTitle}", Toast.LENGTH_SHORT).show()

            // THAY ĐỔI Ở ĐÂY: Gọi openChapterActivity
            currentBookIdForNavigation?.let { bookId ->
                // Giả sử model Chapter của bạn có trường 'id' là ID của chương
                if (selectedChapter.id.isNotBlank()) { // Đảm bảo chapter ID hợp lệ
                    openChapterActivity(bookId, selectedChapter.id)
                } else {
                    Toast.makeText(this, "Chapter ID không hợp lệ.", Toast.LENGTH_SHORT).show()
                }
            } ?: Toast.makeText(this, "Book ID không xác định để mở chương.", Toast.LENGTH_SHORT).show()
        }
        recyclerViewChapters.apply {
            layoutManager = LinearLayoutManager(this@BookActivity,
                LinearLayoutManager.HORIZONTAL, false) // Bạn đang dùng HORIZONTAL, nếu danh sách chương dài, có thể cân nhắc VERTICAL
            adapter = chapterAdapter
        }
    }
}