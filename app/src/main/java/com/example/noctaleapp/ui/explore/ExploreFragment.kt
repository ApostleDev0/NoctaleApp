package com.example.noctaleapp.ui.explore

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.noctaleapp.R
import com.example.noctaleapp.adapter.BookAdapter
import com.example.noctaleapp.databinding.FragmentExploreBinding
import com.example.noctaleapp.model.Book
import com.example.noctaleapp.repository.AuthRepository
import com.example.noctaleapp.repository.BookRepository
import com.example.noctaleapp.ui.BookActivity
import com.example.noctaleapp.ui.ChapterActivity
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.firestore.FirebaseFirestore


class ExploreFragment : Fragment() {
    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!
    private lateinit var chipGroup: ChipGroup
    private val bookRepository = BookRepository()
    private var fullBookList = mutableListOf<Book>()
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var bookAdapter: BookAdapter
    private val authRepository = AuthRepository()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupBookAdapter()
        setupSearchAction()
    }

    private fun setupBookAdapter() {
        bookAdapter = BookAdapter(
            onBookClick = {
                    book ->
                val intent = Intent(requireContext(), BookActivity::class.java)
                intent.putExtra(BookActivity.EXTRA_BOOK_ID, book.id)
                intent.putExtra(BookActivity.EXTRA_UID, authRepository.getCurrentUser()?.uid ?: "")
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
        binding.searchResultData.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = bookAdapter
        }
    }

    private fun setupSearchAction() {
        binding.textSearchInput.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            ) {
                val keyword = binding.textSearchInput.text.toString().trim()

                if (keyword.isNotEmpty()) {
                    performSearch(keyword)
                } else {
                    Toast.makeText(requireContext(), "Nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show()
                }
                true
            } else {
                false
            }
        }
    }

    private fun performSearch(keyword: String) {
        firestore.collection("books")
            .orderBy("title")
            .startAt(keyword)
            .endAt(keyword)
            .get()
            .addOnSuccessListener { result ->
                Log.d("Search", "Found ${result.size()} documents")
                val bookList = result.mapNotNull { doc ->
                    doc.toObject(Book::class.java)
                }
                Log.d("SearchFragment", "Search result: $bookList")
                bookAdapter.updateData(bookList)

                // Ẩn/hiện layout kết quả
                binding.searchResultData.visibility = if (bookList.isEmpty()) View.GONE else View.VISIBLE
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Lỗi tìm kiếm: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

}