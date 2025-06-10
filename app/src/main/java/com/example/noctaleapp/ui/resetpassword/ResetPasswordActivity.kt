package com.example.noctaleapp.ui.resetpassword

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.noctaleapp.R
import com.example.noctaleapp.databinding.ActivityResetPasswordBinding

class ResetPasswordActivity : AppCompatActivity(), ForgotPasswordFragment.OnForgotPasswordListener {

    // khai báo biến binding cho Reset Pass
    private lateinit var binding: ActivityResetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // khởi tạo binding
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.layoutResetPasswordContainer) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Fragments
        // Điều kiện: chỉ thêm fragment khi activity được tạo lần đầu
        // tránh các fragment chồng lên nhau khi xoay màn hình
        if (savedInstanceState == null) {
            // tạo instance của fragment muốn hiển thị
            val forgotPasswordFragment = ForgotPasswordFragment()

            // gắn transaction cho fragment
            supportFragmentManager.beginTransaction().replace(R.id.layout_reset_password_container, forgotPasswordFragment).commit()
        }
    }

    override fun onBackPassword() {
        finish()
    }

}