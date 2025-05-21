package com.example.noctaleapp.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.noctaleapp.R
import com.example.noctaleapp.adapter.BookAdapter
import com.example.noctaleapp.databinding.FragmentHomeBinding
import com.example.noctaleapp.ui.home.HomeFragment
import com.example.noctaleapp.viewmodel.HomeViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.bookList.observe(viewLifecycleOwner) { books ->
            binding.suggetsBook.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = BookAdapter(books)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}