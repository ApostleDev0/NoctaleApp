package com.example.noctaleapp.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.noctaleapp.R
import com.example.noctaleapp.adapter.ProfileTabAdapter
import com.example.noctaleapp.databinding.FragmentProfileBinding
import com.example.noctaleapp.viewmodel.HomeViewModel
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.getValue
import android.widget.*
import com.bumptech.glide.Glide


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var userId = "user20250601"

    private val tabTitles = listOf("Product", "Fan", "Follower")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
   }

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var profileImage: ImageView
    private lateinit var profileName: TextView
    private lateinit var userName: TextView
    private lateinit var profileDescription: TextView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.fetchUserById(userId)

        profileImage = binding.profileImage
        profileName = binding.profileName
        userName = binding.userName
        profileDescription = binding.profileDescription

        viewModel.users.observe(viewLifecycleOwner) { user ->
            profileName.text = user.displayName
//            userName.text = user.username
            profileDescription.text = user.description
            Glide.with(this)
                .load(user.avatarUrl)
                .into(profileImage)
        }

        val adapter = ProfileTabAdapter(this)
        binding.profileViewPage.adapter = adapter
        binding.profileViewPage.isUserInputEnabled = true

        TabLayoutMediator(binding.profileTabLayout, binding.profileViewPage) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}