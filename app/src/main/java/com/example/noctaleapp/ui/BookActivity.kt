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

class BookActivity : AppCompatActivity() {

    private lateinit var bookViewModel: BookViewModel
    private lateinit var imageViewBookCover: ImageView
    private lateinit var textViewBookTitle: TextView
    private lateinit var textViewBookGenre: TextView
    private lateinit var textViewBookDescription: TextView
    private lateinit var textViewChaptersLabel: TextView
    private lateinit var recyclerViewChapters: RecyclerView
    private lateinit var chapterAdapter: ChapterAdapter

    companion object {
        const val EXTRA_BOOK_ID = "extra_book_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book)

        initViews()
        setupRecyclerView()

        bookViewModel = ViewModelProvider(this)[BookViewModel::class.java]

        val bookId = intent.getStringExtra(EXTRA_BOOK_ID)

        if (bookId.isNullOrBlank()) {
            Toast.makeText(this, "Book ID không hợp lệ.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        observeViewModel()
        bookViewModel.loadBookAndChapterDetails(bookId)
    }

    private fun initViews() {
        imageViewBookCover = findViewById(R.id.imageViewBookCover)
        textViewBookTitle = findViewById(R.id.textViewBookTitle)
        textViewBookGenre = findViewById(R.id.textViewBookGenre)
        textViewBookDescription = findViewById(R.id.textViewBookDescription)
        textViewChaptersLabel = findViewById(R.id.textViewChaptersLabel)
        recyclerViewChapters = findViewById(R.id.recyclerViewChapters)
    }

    private fun observeViewModel() {
        bookViewModel.bookDetails.observe(this) { book ->
            if (book != null) {
                displayBookInfo(book)
                textViewChaptersLabel.visibility = View.VISIBLE
                recyclerViewChapters.visibility = View.VISIBLE
            } else {
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

    private fun setupRecyclerView() {
        chapterAdapter = ChapterAdapter { chapter ->
            Toast.makeText(this, "Clicked (RecyclerView): ${chapter.mainTitle}", Toast.LENGTH_SHORT).show()
        }
        recyclerViewChapters.apply {
            layoutManager = LinearLayoutManager(this@BookActivity,
                LinearLayoutManager.HORIZONTAL, false)
            adapter = chapterAdapter
        }
    }
}