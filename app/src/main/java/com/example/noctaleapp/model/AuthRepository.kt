package com.example.noctaleapp.model

import com.google.firebase.auth.FirebaseAuth

class AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Hàm nhận email, mật khẩu và một call back (tờ ghi chú)
    // sau khi gọi Firebase xong, ghi kết quả vào call back đó
    fun loginUser(email: String, password: String, callback: (isSuccess: Boolean, message: String?) -> Unit) {
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener { task ->
                // Nếu thành công thì ghi vào calback kèm lời nhắn
                // nếu không thành công thì ghi vào callback kèm báo lỗi
                if (task.isSuccessful) {
                    callback(true,"Đăng nhập thành công!")
                } else {
                    callback(false,task.exception?.message)
                }
            }
    }
}