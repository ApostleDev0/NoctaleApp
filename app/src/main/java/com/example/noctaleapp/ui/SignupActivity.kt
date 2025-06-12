package com.example.noctaleapp.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.noctaleapp.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {

    // Khai báo biến binding cho signup
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // khởi tạo binding và setContentView bằng binding.root
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.signupRootLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // xử lý sự kiện mũi tên quay về login bằng finish
        binding.imgButtonBackSignup.setOnClickListener {
            finish()
        }
    }
}