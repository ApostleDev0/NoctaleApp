package com.example.noctaleapp.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.example.noctaleapp.R
import com.bumptech.glide.Glide
import com.example.noctaleapp.ui.LoginActivity
import com.example.noctaleapp.viewmodel.HomeViewModel
import com.example.noctaleapp.adapter.ProfileTabAdapter
import com.example.noctaleapp.databinding.FragmentProfileBinding
import com.example.noctaleapp.ui.SettingActivity
import com.google.android.material.tabs.TabLayoutMediator

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by activityViewModels()
    private val tabTitles = listOf("Product", "Fan", "Follower")

    private lateinit var profileImage: ImageView
    private lateinit var profileName: TextView
    private lateinit var userName: TextView
    private lateinit var profileDescription: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
   }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root = binding.root
        binding.settingButton.setOnClickListener {
            val intentSetting = Intent(requireContext(), SettingActivity::class.java)
            startActivity(intentSetting)
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.uid.observe(viewLifecycleOwner) { uID ->
            if (uID.isNotEmpty()) {
                viewModel.fetchUserById(uID)
            }
        }
        renderProfile()
        val adapter = ProfileTabAdapter(this)
        binding.profileViewPage.adapter = adapter
        binding.profileViewPage.isUserInputEnabled = true

        TabLayoutMediator(binding.profileTabLayout, binding.profileViewPage) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        setupTabsAndViewPager()
    }


    private fun setupTabsAndViewPager() {
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

    private fun renderProfile() {

        profileImage = binding.profileImage
        profileName = binding.profileName
        userName = binding.userName
        profileDescription = binding.profileDescription

        viewModel.users.observe(viewLifecycleOwner) { user ->
            profileName.text = user.displayName
            userName.text = user.username
            profileDescription.text = user.description
            Glide.with(this)
                .load(user.avatarUrl)
                .into(profileImage)
        }
    }
}

