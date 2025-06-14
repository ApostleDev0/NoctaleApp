package com.example.noctaleapp.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.marginTop
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.noctaleapp.adapter.BookByGenreAdapter
import com.example.noctaleapp.adapter.GenreAdapter
import com.example.noctaleapp.databinding.FragmentHomeBinding
import com.example.noctaleapp.viewmodel.HomeViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var userId = "user20250601"

    private val viewModel: HomeViewModel by activityViewModels()

    private lateinit var bookOnReadName: TextView
    private lateinit var bookOnReadAuthor: TextView
    private lateinit var bookOnReadImg: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bookOnReadName = binding.bookOnReadName
        bookOnReadAuthor = binding.bookOnReadAuthor
        bookOnReadImg = binding.bookOnReadImg
        if (viewModel.books.value == null) {
            viewModel.fetchRecentBookByUser(userId)
        }

        viewModel.books.observe(viewLifecycleOwner) { bookData ->
            bookOnReadName.text = bookData.title
            bookOnReadAuthor.text = "Tác giả: ${bookData.author}"
            Glide.with(this)
                .load(bookData.coverUrl)
                .into(bookOnReadImg)
        }

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
            } else {
                binding.booksByGenres.visibility = View.VISIBLE
                binding.emptyViewBookGenres.visibility = View.GONE
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

        viewModel.books.observe(viewLifecycleOwner) {

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}