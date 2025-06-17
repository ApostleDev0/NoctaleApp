package com.example.noctaleapp.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.noctaleapp.adapter.BookByGenreAdapter
import com.example.noctaleapp.adapter.GenreAdapter
import com.example.noctaleapp.adapter.SuggestBooksAdapter
import com.example.noctaleapp.databinding.FragmentHomeBinding
import com.example.noctaleapp.extension.dpToPx
import com.example.noctaleapp.ui.BookActivity
import com.example.noctaleapp.ui.ChapterActivity
import com.example.noctaleapp.viewmodel.HomeViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
//    private var userId = "HYr8ZD3ycnRBKJIshiK2yuZi99w1"
    private val viewModel: HomeViewModel by activityViewModels()

    private lateinit var bookOnReadName: TextView
    private lateinit var bookOnReadAuthor: TextView
    private lateinit var bookOnReadImg: ImageView

    private lateinit var suggestBooksAdapter: SuggestBooksAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.uid.observe(viewLifecycleOwner) { uID ->
            if (uID.isNotEmpty()) {
                if (viewModel.books.value == null) {
                    viewModel.fetchRecentBookByUser(uID)
                }
                if (viewModel.users.value == null) {
                    viewModel.fetchUserById(uID)
                }
            }
        }

        hiUser()
        renderRecentBook()
        renderGenresLayout()

//        viewModel.books.observe(viewLifecycleOwner) {}

        viewModel.fetchSuggestBooks()
        renderSuggestBooks()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun renderSuggestBooks() {
        suggestBooksAdapter = SuggestBooksAdapter(
            onBookClick = {
                book ->
                val intent = Intent(requireContext(), BookActivity::class.java)
                intent.putExtra(BookActivity.EXTRA_BOOK_ID, book.id)
                startActivity(intent)
            },
            onReadNowClick = {
                book ->
                val intent = Intent(requireContext(), ChapterActivity::class.java)
                intent.putExtra(ChapterActivity.EXTRA_BOOK_ID, book.id)
                intent.putExtra(ChapterActivity.EXTRA_CHAPTER_ID, "1_${book.id}")
                startActivity(intent)
            },
            books = mutableListOf(),
        )

        viewModel.suggestBooks.observe(viewLifecycleOwner) {
                books ->
            binding.suggestBook.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = suggestBooksAdapter
            }
            suggestBooksAdapter.updateData(books)
            binding.txtEndSuggestBooks.visibility = if (viewModel.isLastSuggestPage()) View.VISIBLE else View.GONE
        }

        binding.suggestBook.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                if (lastVisibleItem == totalItemCount - 1) {
                    viewModel.fetchSuggestBooks()
                }
            }
        })
    }

    private fun renderGenresLayout() {
        val suggestBookLabel = (binding.suggestBookLabel.layoutParams as ViewGroup.MarginLayoutParams)

        viewModel.genres.observe(viewLifecycleOwner) {
                genres ->
            binding.genreList.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = GenreAdapter(genres) {
                        selectedGenre ->
                    viewModel.fetchBooksByGenre(selectedGenre.name)
                    Log.d("GenreAdapter", "Selected genre: ${selectedGenre.name}, Selected id: ${selectedGenre.id}")
                }
            }
        }
        viewModel.bookGenres.observe(viewLifecycleOwner) { books ->
            if (books.isNullOrEmpty()) {
                binding.booksByGenres.visibility = View.GONE
                binding.emptyViewBookGenres.visibility = View.VISIBLE
                suggestBookLabel.topMargin = requireContext().dpToPx(180f)
            } else {
                binding.booksByGenres.visibility = View.VISIBLE
                binding.emptyViewBookGenres.visibility = View.GONE
                suggestBookLabel.topMargin = requireContext().dpToPx(15f)
                binding.booksByGenres.apply {
                    layoutManager = LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                    adapter = BookByGenreAdapter(books) {

                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun renderRecentBook() {
        bookOnReadName = binding.bookOnReadName
        bookOnReadAuthor = binding.bookOnReadAuthor
        bookOnReadImg = binding.bookOnReadImg

        if (viewModel.books.value != null) {
            binding.recentBookLayout.visibility = View.VISIBLE
        } else {
            binding.recentBookLayout.visibility = View.GONE
        }

        viewModel.books.observe(viewLifecycleOwner) { bookData ->
            bookOnReadName.text = bookData.title
            bookOnReadAuthor.text = "Tác giả: ${bookData.author}"
            Glide.with(this)
                .load(bookData.coverUrl)
                .into(bookOnReadImg)
        }
    }

    private fun hiUser() {
        viewModel.users.observe(viewLifecycleOwner) { user ->
            binding.welcomeUser.text = "Hi, ${user.username}"
        }
    }
}