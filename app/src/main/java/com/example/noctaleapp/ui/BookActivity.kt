package com.example.noctaleapp.ui

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
import android.widget.ImageButton
import android.util.Log
import android.widget.ProgressBar
import com.example.noctaleapp.repository.BookRepository
import com.example.noctaleapp.viewmodel.BookViewModelFactory
import com.example.noctaleapp.viewmodel.HomeViewModel
import com.example.noctaleapp.repository.GenreRepository

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
    private lateinit var imgButtonReturn: ImageButton
    private lateinit var addToLibrary: ImageButton

    private val homeViewModel = HomeViewModel()

    private var currentBookIdForNavigation: String? = null
    private var uId: String = ""

    companion object {
        const val EXTRA_UID = "uid"
        const val EXTRA_BOOK_ID = "extra_book_id"
        const val EXTRA_FOCUS_CHAPTERS = "com.example.noctaleapp.ui.FOCUS_CHAPTER_LIST"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book)

        initViews()
        addToLibrary = findViewById(R.id.addToLibrary)

        val receivedBookId = intent.getStringExtra(EXTRA_BOOK_ID)
        val uid = intent.getStringExtra(EXTRA_UID)
        Log.d("BookActivity", "Received Book ID: $uid")
        if (receivedBookId.isNullOrBlank()  || uid.isNullOrBlank()) {
            Toast.makeText(this, "Book ID không hợp lệ.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        currentBookIdForNavigation = receivedBookId


        uId = uid

        setupRecyclerView()
        val bookRepository = BookRepository()
        val genreRepository = GenreRepository()
        val factory = BookViewModelFactory(bookRepository, genreRepository)
        bookViewModel = ViewModelProvider(this, factory)[BookViewModel::class.java]
        val viewModelFactory = BookViewModelFactory(bookRepository, genreRepository)
        bookViewModel = ViewModelProvider(this, viewModelFactory)[BookViewModel::class.java]

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

        imgButtonReturn.setOnClickListener {
            finish()
        }
    }

    private fun initViews() {
        progressBarBookLoading = findViewById(R.id.progressBarBookLoading)
        imageViewBookCover = findViewById(R.id.imageViewBookCover)
        textViewBookTitle = findViewById(R.id.textViewBookTitle)
        textViewBookGenre = findViewById(R.id.textViewBookGenre)
        textViewBookDescription = findViewById(R.id.textViewBookDescription)
        textViewChaptersLabel = findViewById(R.id.textViewChaptersLabel)
        recyclerViewChapters = findViewById(R.id.recyclerViewChapters)
        imgButtonReturn = findViewById(R.id.imgButtonReturn)
    }

    private fun observeViewModel() {
        bookViewModel.isLoadingBook.observe(this) { isLoading ->
            if (isLoading) {
                progressBarBookLoading.visibility = View.VISIBLE
            } else {
                progressBarBookLoading.visibility = View.GONE
            }
        }
        bookViewModel.bookDetails.observe(this) { book ->
            if (book != null) {
                homeViewModel.checkIfBookInLibrary(uId, book.id) {
                    isInLibrary ->
                    updateLibraryIcon(isInLibrary)

                    addToLibrary.setOnClickListener {
                        homeViewModel.toggleBookInLibrary(uId, book) {
                            updateStatus ->
                            updateLibraryIcon(updateStatus)

                            val msg = if (updateStatus) "Đã thêm vào thư viện" else "Đã xoá khỏi thư viện"
                            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                displayBookInfo(book)
                textViewChaptersLabel.visibility = View.VISIBLE
                recyclerViewChapters.visibility = View.VISIBLE
                imageViewBookCover.visibility = View.VISIBLE
                textViewBookTitle.visibility = View.VISIBLE
            } else {
                imageViewBookCover.visibility = View.GONE
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
        bookViewModel.allGenres.observe(this) { genres ->
            if (bookViewModel.bookDetails.value != null && genres != null) {
                displayBookInfo(bookViewModel.bookDetails.value!!)
            }
        }

        bookViewModel.isLoadingGenres.observe(this) { isLoading ->
            if (isLoading && bookViewModel.bookDetails.value?.genres?.isNotEmpty() == true) {
                if (textViewBookGenre.text.toString().contains("Không có") || textViewBookGenre.text.toString().contains("Không xác định")) {
                    textViewBookGenre.text = "Thể loại: Đang tải..."
                }
            }
        }
    }

    private fun displayBookInfo(book: Book) {
        textViewBookTitle.text = book.title
        val genreNames = bookViewModel.getGenreNamesForCurrentBook()

        if (genreNames.isNotEmpty()) {
            textViewBookGenre.text = "Thể loại: ${genreNames.joinToString(", ")}"
        } else if (book.genres.isNotEmpty() && (bookViewModel.allGenres.value.isNullOrEmpty() && bookViewModel.isLoadingGenres.value == true)) {
            textViewBookGenre.text = "Thể loại: Đang tải..."
        } else if (book.genres.isNotEmpty() && bookViewModel.allGenres.value.isNullOrEmpty()) {
            textViewBookGenre.text = "Thể loại: Không xác định"
            Log.w("BookActivity", "Book has genre IDs but no names resolved. Genre IDs: ${book.genres}, AllGenres loaded: ${bookViewModel.allGenres.value?.size ?: 0}")
        }
        else {
            textViewBookGenre.text = "Thể loại: Không có"
        }
        textViewBookDescription.text = book.description

        Glide.with(this)
            .load(book.coverUrl)
            .placeholder(R.drawable.ic_bookcover)
            .error(R.drawable.ic_bookcover)
            .into(imageViewBookCover)
    }

    private fun openChapterActivity(bookId: String, chapterId: String) {
        val intent = Intent(this, ChapterActivity::class.java).apply {
            putExtra(ChapterActivity.EXTRA_BOOK_ID, bookId)
            putExtra(ChapterActivity.EXTRA_CHAPTER_ID, chapterId)
        }
        startActivity(intent)
    }


    private fun setupRecyclerView() {
        chapterAdapter = ChapterAdapter { selectedChapter ->
            currentBookIdForNavigation?.let { bookId ->

                if (selectedChapter.id.isNotBlank()) {
                    openChapterActivity(bookId, selectedChapter.id)
                } else {
                    Toast.makeText(this, "Chapter ID không hợp lệ.", Toast.LENGTH_SHORT).show()
                }
            } ?: Toast.makeText(this, "Book ID không xác định để mở chương.", Toast.LENGTH_SHORT).show()
        }
        recyclerViewChapters.apply {
            layoutManager = LinearLayoutManager(this@BookActivity,
                LinearLayoutManager.VERTICAL, false)
            adapter = chapterAdapter
        }
    }

    fun updateLibraryIcon(isInLibrary: Boolean) {
        if (isInLibrary) {
            addToLibrary.setImageResource(R.drawable.ic_library_added)
        } else {
            addToLibrary.setImageResource(R.drawable.ic_library_add)
        }
    }
}