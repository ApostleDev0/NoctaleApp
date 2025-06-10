package com.example.noctaleapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.noctaleapp.model.AuthRepository

class LoginViewModel : ViewModel() {
    private val repository = AuthRepository()

    // khai báo loginstatus, chỉ có loginviewmodel mới được ghi lên
    private val _loginStatus = MutableLiveData<String>()
    // bảng công khai show tên loginactivity
    val loginStatus: LiveData<String> = _loginStatus

    fun login(email: String, password: String) {
        // thông báo bắt đầu đăng nhập
        _loginStatus.value = "LOADING"

        // firebase sẽ kiểm tra đăng nhập
        repository.loginUser(email, password) { isSuccess, message ->
            // thông báo thành công: "THÀNH CÔNG"
            // thông báo thất bại: "Lỗi: [lý do]"
            if (isSuccess) {
                _loginStatus.value = "Thành công!"
            } else {
                _loginStatus.value = "ERROR: ${message ?: "Lỗi không xác định"}"
            }
        }
    }

}