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
        // thông báo trạng thái "đang xử lý" bằng hằng số
        _loginStatus.value = STATUS_LOADING

        // yêu cầu repository đăng nhập
        repository.loginUser(email, password) { isSuccess, message ->
            if (isSuccess) {
                _loginStatus.value = STATUS_SUCCESS // nếu thành công, thông báo thành công
            } else { // nếu thất bại, thông báo lỗi với tin nhắn từ firebase
                _loginStatus.value = "$STATUS_ERROR_PREFIX${message ?: "Lỗi không xác định"}"
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