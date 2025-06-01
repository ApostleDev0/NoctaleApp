package com.example.noctaleapp.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.noctaleapp.ui.profile.FanProfileFragment
import com.example.noctaleapp.ui.profile.FollowerProfileFragment
import com.example.noctaleapp.ui.profile.ProductProfileFragment
import com.example.noctaleapp.ui.profile.ProfileFragment

class ProfileTabAdapter(fragment: ProfileFragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ProductProfileFragment()
            1 -> FanProfileFragment()
            2 -> FollowerProfileFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")

        }
    }
}