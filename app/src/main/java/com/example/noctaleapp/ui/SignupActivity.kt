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

    // Khai báo biến binding cho signup
    private lateinit var binding: ActivitySignupBinding
    // khởi tạo viewmodel
    private val viewModel: SignupViewModel by viewModels()

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

        // thiết lập Listener cho nút "Đăng ký" và nút quay lại
        setupClickListener()
        // lắng nghe và thay đổi các trạng thái từ viewmodel
        observeViewModel()
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
            val phone = binding.editTextPhoneSignup.text.toString().trim()
            val password = binding.editTextPasswordSignup.text.toString()
            val confirmPassword = binding.editTextConfirmPasswordSignup.text.toString()

            // Gọi hàm signUp trong ViewModel để xử lý
            viewModel.signUp(username, email, phone, password, confirmPassword)
        }
    }

    // Hàm quan sát Livedata từ ViewModel để cập nhật giao diện
    private fun observeViewModel() {
        viewModel.signupStatus.observe(this) { status ->
            when {
                // trạng thái Đang xử lý
                status == SignupViewModel.STATUS_LOADING -> {
                    binding.btnSignupSubmit.isEnabled = false
                    binding.btnSignupSubmit.text = "Đang xử lý..."
                }
                // trạng thái Đăng ký thành công
                status == SignupViewModel.STATUS_SUCCESS -> {
                    Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()

                    // Chuyển về màn hình đăng nhập và xóa các activity trước đó
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                // trạng thái có lỗi xảy ra
                status.startsWith(SignupViewModel.STATUS_ERROR_PREFIX) -> {
                    binding.btnSignupSubmit.isEnabled = true
                    // Lấy lại văn bản gốc của nút từ file strings.xml
                    // Trong file XML của bạn, nút này đang dùng string "sign_in"
                    binding.btnSignupSubmit.text = getString(R.string.sign_in)

                    val errorMessage = status.removePrefix(SignupViewModel.STATUS_ERROR_PREFIX)
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}