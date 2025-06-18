package com.example.noctaleapp.ui

import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.noctaleapp.R
import com.example.noctaleapp.viewmodel.HomeViewModel

class SettingActivity : AppCompatActivity() {
    private lateinit var SignOutButton: AppCompatButton
    private lateinit var EditProfileButton: AppCompatButton
    private lateinit var ReturnButton: ImageButton
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_setting)

        SignOutButton = findViewById(R.id.SignOutButton)
        ReturnButton = findViewById(R.id.imgButtonReturn)
        EditProfileButton = findViewById(R.id.editProfileButton)

        SignOutButton.setOnClickListener {
            homeViewModel.logout()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        ReturnButton.setOnClickListener {
            finish()
        }

        EditProfileButton.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}