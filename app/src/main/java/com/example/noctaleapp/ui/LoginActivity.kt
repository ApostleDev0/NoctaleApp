package com.example.noctaleapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
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
        setupWindowInsets()

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
        // Lắng nghe các sự kiện click từ người dùng
        setupClickListeners()

        // Quan sát sự thay đổi trạng thái từ ViewModel
        observeLoginStatus()


    }
    private fun setupClickListeners() {
        binding.txtViewForgetPasswordLogin.setOnClickListener {
            startActivity(Intent(this, ResetPasswordActivity::class.java))
        }

        binding.txtViewSignupNow.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.txtInputUsernameLogin.text.toString().trim()
            val password = binding.txtInputPasswordLogin.text.toString().trim()
            if (validateInput(email, password)) {
                loginViewModel.login(email, password)
            }
        }

    }

    private fun observeLoginStatus() {
        loginViewModel.loginStatus.observe(this) { status ->
            // Khi có thông báo mới, kiểm tra xem nó là gì bằng cách so sánh với các hằng số
            when {
                // Nếu trạng thái là LOADING
                status == LoginViewModel.STATUS_LOADING -> {
                    binding.btnLogin.isEnabled = false // Khóa nút đăng nhập lại
                    Toast.makeText(this, "Đang kiểm tra...", Toast.LENGTH_SHORT).show()
                }
                // Nếu trạng thái là SUCCESS
                status == LoginViewModel.STATUS_SUCCESS -> {
                    binding.btnLogin.isEnabled = true // Mở lại nút
                    Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish() // Đóng màn hình này lại
                }
                // Nếu trạng thái bắt đầu bằng chuỗi ERROR
                status.startsWith(LoginViewModel.STATUS_ERROR_PREFIX) -> {
                    binding.btnLogin.isEnabled = true // Mở lại nút
                    // Lấy tin nhắn lỗi ra khỏi chuỗi trạng thái
                    val errorMessage = status.substringAfter(LoginViewModel.STATUS_ERROR_PREFIX)
                    Toast.makeText(this, "Lỗi: $errorMessage", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    private fun LoginActivity.validateInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            binding.textInputLayoutUsernameLogin.error = "Vui lòng nhập email!"
            binding.textInputLayoutUsernameLogin.requestFocus()
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.textInputLayoutUsernameLogin.error = "Email không hợp lệ"
            binding.textInputLayoutUsernameLogin.requestFocus()
            return false
        }
        if (password.isEmpty()) {
            binding.textInputLayoutPasswordLogin.error = "Vui lòng nhập password!"
            binding.textInputLayoutPasswordLogin.requestFocus()
            return false
        }
        return true
    }
    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.loginRootLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}

