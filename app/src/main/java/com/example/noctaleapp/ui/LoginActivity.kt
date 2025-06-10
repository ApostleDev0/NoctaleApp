package com.example.noctaleapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.activity.viewModels
import androidx.core.view.WindowInsetsCompat
import com.example.noctaleapp.databinding.ActivityLoginBinding
import com.example.noctaleapp.ui.resetpassword.ResetPasswordActivity
import com.example.noctaleapp.viewmodel.LoginViewModel


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding // khai báo biến binding cho login

    // quản lý riêng của login
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Khởi tạo binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        ViewCompat.setOnApplyWindowInsetsListener(binding.loginRootLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Xử lý sự kiện click cho "Quên mật khẩu"
        binding.txtViewForgetPasswordLogin.setOnClickListener {
            val intent = Intent(this, ResetPasswordActivity::class.java)
            startActivity(intent)
        }

        // Xử lý sự kiện cho nút "Đăng ký ngay"
        binding.txtViewSignupNow.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        // Xử lý sự kiện click cho "Đăng nhập"
        binding.btnLogin.setOnClickListener {
            val email = binding.txtInputUsernameLogin.text.toString().trim()
            val password = binding.txtInputPasswordLogin.text.toString().trim()
            if (validateInput(email, password)) {
                loginViewModel.login(email, password)
            }
        }

        loginViewModel.loginStatus.observe(this) { status ->
            // Khi có thông báo mới, kiểm tra xem nó là gì
            when {
                status == "LOADING" -> {
                    // Quản lý báo "Đang xử lý", thì vô hiệu hóa nút bấm
                    binding.btnLogin.isEnabled = false
                    Toast.makeText(this, "Đang kiểm tra...", Toast.LENGTH_SHORT).show()
                }
                status == "SUCCESS" -> {
                    // Quản lý báo "Thành công", thì chuyển màn hình
                    binding.btnLogin.isEnabled = true
                    Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                status.startsWith("ERROR:") -> {
                    // Quản lý báo "Lỗi", thì hiển thị lỗi
                    binding.btnLogin.isEnabled = true
                    val errorMessage = status.substringAfter("ERROR: ")
                    Toast.makeText(this, "Lỗi: $errorMessage", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    private fun validateInput(email: String, password: String): Boolean {
        // kiểm tra trường nhập trống hay không
        if (email.isEmpty()) {
            binding.txtInputUsernameLogin.error = "Vui lòng nhập email"
            binding.txtInputUsernameLogin.requestFocus()
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.txtInputUsernameLogin.error = "Email không hợp lệ"
            binding.txtInputUsernameLogin.requestFocus()
            return false
        }
        if (password.isEmpty()) {
            binding.txtInputPasswordLogin.error = "Vui lòng nhập mật khẩu"
            binding.txtInputPasswordLogin.requestFocus()
            return false
        }
        return true
    }
}