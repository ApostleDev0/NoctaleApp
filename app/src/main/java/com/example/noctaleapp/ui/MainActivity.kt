package com.example.noctaleapp.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.noctaleapp.R
import com.example.noctaleapp.databinding.ActivityMainBinding
import com.example.noctaleapp.ui.explore.ExploreFragment
import com.example.noctaleapp.ui.home.HomeFragment
import com.example.noctaleapp.ui.library.LibraryFragment
import com.example.noctaleapp.ui.profile.ProfileFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        replaceViewFragment(HomeFragment())
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.nav_home -> replaceViewFragment(HomeFragment())
                R.id.nav_library -> replaceViewFragment(LibraryFragment())
                R.id.nav_explore -> replaceViewFragment(ExploreFragment())
                R.id.nav_profile -> replaceViewFragment(ProfileFragment())

                else -> {
                }
            }
            true
        }

    }

    private fun replaceViewFragment(fragment : Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}