package com.example.noctaleapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.noctaleapp.R
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.noctaleapp.databinding.ActivitySignupBinding
import com.example.noctaleapp.viewmodel.SignupViewModel

class SignupActivity : AppCompatActivity() {


    private lateinit var binding: ActivitySignupBinding // Khai báo biến binding cho signup
    private val viewModel: SignupViewModel by viewModels() // khởi tạo viewmodel

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

        setupClickListener() // thiết lập Listener cho nút "Đăng ký" và nút quay lại
        observeViewModel() // lắng nghe và thay đổi các trạng thái từ viewmodel
    }

    // hàm tập trung xử lý sự kiện click
    private fun setupClickListener() {
        // sự kiện nút quay lại
        binding.imgButtonBackSignup.setOnClickListener {
            finish() // đóng activity hiện tại và quay về màn hình trước
        }

        // sự kiện nút "Đăng ký"
        binding.btnSignupSubmit.setOnClickListener {
            // Lấy dữ liệu người dùng nhập từ các EditText
            val username = binding.editTextUsernameSignup.text.toString().trim()
            val email = binding.editTextEmailSignup.text.toString().trim()
            //val phone = ""
            val password = binding.editTextPasswordSignup.text.toString().trim()
            val confirmPassword = binding.editTextConfirmPasswordSignup.text.toString().trim()

            // Gọi hàm validate trước
            if (validateInput(username, email, password, confirmPassword)) {
                // nếu hợp lệ mới gọi Viewmodel để đăng ký
                viewModel.signUp(username, email, password, confirmPassword)
            }
        }
    }

    // Hàm lắng nghe trạng thái từ ViewModel để cập nhật Ui
    private fun observeViewModel() {
        viewModel.signupStatus.observe(this) { status ->
            when {
                status == SignupViewModel.STATUS_LOADING -> {
                    binding.btnSignupSubmit.isEnabled = false
                    binding.btnSignupSubmit.text = "Đang xử lý..."
                }
                status == SignupViewModel.STATUS_SUCCESS -> {
                    Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()

                    // Chuyển về màn hình đăng nhập và xóa các activity trước đó
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                status.startsWith(SignupViewModel.STATUS_ERROR_PREFIX) -> {
                    binding.btnSignupSubmit.isEnabled = true
                    binding.btnSignupSubmit.text = getString(R.string.sign_in)
                    val errorMessage = status.removePrefix(SignupViewModel.STATUS_ERROR_PREFIX)
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    // hàm check validate input
    private fun validateInput(username: String, email: String, pass: String, confirmPass: String): Boolean {
        // Biểu thức chính quy cho username và password
        val usernameRegex = "^[a-zA-Z0-9_.]{6,30}$".toRegex()
        val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$".toRegex()

        var isValid = true // biến cờ

        if (!username.matches(usernameRegex)) {
            binding.textInputLayoutUsernameSignup.error = "Username dài 6-30 ký tự, chỉ chứa chữ, số, '.', '_'"
            isValid = false
        } else {
            binding.textInputLayoutUsernameSignup.error = null
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.textInputLayoutEmailSignup.error = "Địa chỉ email không hợp lệ."
            isValid = false
        } else {
            binding.textInputLayoutEmailSignup.error = null
        }

        if (!pass.matches(passwordRegex)) {
            binding.textInputLayoutPasswordSignup.error = "Mật khẩu tối thiểu 8 ký tự, gồm chữ hoa, thường, số và ký tự đặc biệt."
            isValid = false
        } else {
            binding.textInputLayoutPasswordSignup.error = null
        }

        if (pass != confirmPass) {
            binding.textInputLayoutConfirmPasswordSignup.error = "Mật khẩu xác nhận không khớp."
            isValid = false
        } else {
            binding.textInputLayoutConfirmPasswordSignup.error = null
        }

        // Nếu tất cả đều hợp lệ, trả về true
        return isValid
    }
}