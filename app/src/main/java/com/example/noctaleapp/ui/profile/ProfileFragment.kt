package com.example.noctaleapp.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.noctaleapp.R
import com.example.noctaleapp.adapter.ProfileTabAdapter
import com.example.noctaleapp.databinding.FragmentProfileBinding
import com.google.android.material.tabs.TabLayoutMediator


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val tabTitles = listOf("Product", "Fan", "Follower")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
   }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ProfileTabAdapter(this)
        binding.profileViewPage.adapter = adapter

        TabLayoutMediator(binding.profileTabLayout, binding.profileViewPage) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}