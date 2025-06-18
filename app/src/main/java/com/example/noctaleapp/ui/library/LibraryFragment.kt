package com.example.noctaleapp.ui.library

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.noctaleapp.R
import com.example.noctaleapp.adapter.LibraryBookAdapter
import com.example.noctaleapp.databinding.FragmentLibraryBinding
import com.example.noctaleapp.model.LibraryBook
import com.example.noctaleapp.ui.BookActivity
import com.example.noctaleapp.viewmodel.LibraryViewModel
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.firebase.auth.FirebaseAuth

class LibraryFragment : Fragment() {
    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LibraryViewModel by activityViewModels()

    private lateinit var libraryBookAdapter: LibraryBookAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        libraryBookAdapter = LibraryBookAdapter(
            mutableListOf(),
            onLongClick = { book ->
                showDeleteDialog(book)
            },
            onSaveClick = { book ->
                Toast.makeText(requireContext(), "Mở khoá Vip để sử dụng tính năng!", Toast.LENGTH_SHORT).show()
            },
            onItemClick = { book ->
                val intent = Intent(requireContext(), BookActivity::class.java)
                intent.putExtra(BookActivity.EXTRA_BOOK_ID, book.id)
                intent.putExtra(BookActivity.EXTRA_UID, uid)
                startActivity(intent)
            }
        )

        binding.libraryBook.apply {
            layoutManager = FlexboxLayoutManager(requireContext()).apply{
                flexWrap = FlexWrap.WRAP
                justifyContent = JustifyContent.FLEX_START
            }
            adapter = libraryBookAdapter
        }

        viewModel.getLibraryBooks(uid).observe(viewLifecycleOwner) { books ->
            libraryBookAdapter.updateData(books)
        }
    }

    fun showDeleteDialog(book: LibraryBook) {
        AlertDialog.Builder(requireContext())
            .setTitle("Xoá sách")
            .setMessage("Bạn có chắc muốn xoá sách này khỏi thư viện?")
            .setPositiveButton("Xoá") { _, _ ->
                val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@setPositiveButton
                Log.d("DELETE_BOOK", "UID: $uid | BookID: ${book.id}")
                viewModel.removeBookFromLibrary(uid, book.id)
            }
            .setNegativeButton("Huỷ", null)
            .show()
    }
}