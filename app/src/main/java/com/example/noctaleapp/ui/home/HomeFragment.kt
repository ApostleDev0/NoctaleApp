package com.example.noctaleapp.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.noctaleapp.databinding.FragmentHomeBinding
import com.example.noctaleapp.viewmodel.HomeViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var userId = "user20250601"

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var bookOnReadName: TextView
    private lateinit var bookOnReadAuthor: TextView
    private lateinit var bookOnReadImg: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bookOnReadName = binding.bookOnReadName
        bookOnReadAuthor = binding.bookOnReadAuthor
        bookOnReadImg = binding.bookOnReadImg
        viewModel.fetchRecentBookByUser(userId)

        viewModel.books.observe(viewLifecycleOwner) { bookData ->
            bookOnReadName.text = bookData.title
            bookOnReadAuthor.text = "Tác giả: ${bookData.author}"
            Glide.with(this)
                .load(bookData.coverUrl)
                .into(bookOnReadImg)
        }
//        viewModel.bookList.observe(viewLifecycleOwner) { books ->
//            binding.suggetsBook.apply {
//                layoutManager = LinearLayoutManager(requireContext())
//                adapter = HomeAdapter(books)
//            }
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

//i'm here. ahhh shiet
// cái này chắc phải thêm vào database nữa thì mới ược
