package com.example.noctaleapp.viewmodel

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.noctaleapp.repository.AuthRepository

class SignupViewModel: ViewModel() {
    private val repository = AuthRepository()

    // khai báo signupStatus, chỉ có ViewModel này mới được ghi lên
    private val _signupStatus = MutableLiveData<String>()

    // signupStatus công khai để SignupActivity theo dõi trạng thái
    val signupStatus: LiveData<String> = _signupStatus

    //Hàm được gọi từ Activity để bắt đầu quá trình đăng ký
    fun signUp(username: String, email: String, phone: String, pass: String, confirmPass: String) {
        // Logic kiểm tra dữ liệu (Validate) nằm ở đây

        // nếu trường nhập dữ liệu trông -> Thông báo lỗi
        if (username.isEmpty() || email.isEmpty() || phone.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()) {
            _signupStatus.value = "${STATUS_ERROR_PREFIX}Vui lòng nhập đầy đủ thông tin."
            return
        }
        // kiểm tra email có hợp lệ
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _signupStatus.value = "${STATUS_ERROR_PREFIX}Địa chỉ email không hợp lệ."
            return
        }
        // kiểm tra độ dài tối thiểu của pass là 6
        if (pass.length < 6) {
            _signupStatus.value = "${STATUS_ERROR_PREFIX}Mật khẩu phải có ít nhất 6 ký tự."
            return
        }
        // kiểm tra nhập lại pass có khớp
        if (pass != confirmPass) {
            _signupStatus.value = "${STATUS_ERROR_PREFIX}Mật khẩu xác nhận không khớp."
            return
        }

        // nếu các trường hợp lệ thì gọi Repository
        _signupStatus.value = STATUS_LOADING

        // yêu cầu repository thực hiện đăng ký
        repository.registerUser(username, email, phone, pass) { isSuccess, message ->
            if (isSuccess) {
                // Nếu thành công, thông báo thành công
                _signupStatus.value = STATUS_SUCCESS
            } else {
                // Nếu thất bại, thông báo lỗi với tin nhắn từ Firebase
                _signupStatus.value = "$STATUS_ERROR_PREFIX${message ?: "Lỗi không xác định"}"
            }
        }
    }

    // hàm chứa các hằng số trạng thái
    companion object {
        const val STATUS_LOADING = "LOADING"
        const val STATUS_SUCCESS = "SUCCESS"
        const val STATUS_ERROR_PREFIX = "ERROR: "
    }
}