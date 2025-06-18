package com.example.noctaleapp.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseUser

class AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    /**
     * Hàm đăng nhập người dùng bằng email và mật khẩu.
     * @param callback Là một "tờ ghi chú" để báo lại kết quả (thành công/thất bại và lời nhắn).
     */
    fun loginUser(email: String, password: String, callback: (isSuccess: Boolean, message: String?) -> Unit) {
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener { task ->
                // nếu tác vụ thành công thì ghi vào call back là "true" và gửi lời nhắn
                if (task.isSuccessful) {
                    callback(true,"Đăng nhập thành công!")
                } else { // nếu không, ghi vào call back là "false" và gửi thông báo lỗi từ Firebase
                    callback(false,task.exception?.message)
                }
            }
    }
    /**
     * Hàm đăng ký người dùng mới và lưu thông tin vào Firestore.
     * @param callback Tương tự như hàm login, dùng để báo lại kết quả.
     */
    fun registerUser(username: String, email: String, phone: String, pass: String, avatarUrl: String = "https://osadkjpbnyvrlpptmjcw.supabase.co/storage/v1/object/public/noctale.profile.image/default/slay_profile.jpg", callback: (isSuccess: Boolean, message: String?) -> Unit) {
        // Dùng FirebaseAuth để tạo tài khoản mới
        auth.createUserWithEmailAndPassword(email,pass)
            .addOnCompleteListener { task ->
                // nếu đăng ký thành công, lấy ID người dùng mới
                if (task.isSuccessful){
                    val uid = auth.currentUser?.uid

                    if (uid != null) {
                        // chuẩn bị bộ hồ sơ người dùng để lưu trữ
                        val userProfile = hashMapOf(
                            "uid" to uid,
                            "username" to username,
                            "email" to email,
                            "phone" to phone,
                            "avatarUrl" to avatarUrl
                        )

                        // lưu trữ bộ hồ sơ vào Firestore
                        firestore.collection("users").document(uid)
                            .set(userProfile)
                            .addOnSuccessListener {
                                // Nếu lưu thành công -> Thông báo toàn bộ quá trình thành công
                                callback(true, "Đăng ký thành công!")
                            }
                            .addOnFailureListener { e ->
                                // Lỗi khi đang lưu hồ sơ -> Thông báo lỗi
                                callback(false, "Lỗi khi đang lưu thông tin: ${e.message}!")
                            }
                    } else {
                        // trường hợp hiếm gặp: tạo user thành công nhưng không lấy được ID
                        callback(false, "Không thể lấy được ID người dùng!")
                    }
                } else {
                    // Đăng ký thất bại ( email đã tồn tại)
                    callback(false, task.exception?.message)
                }
            }
    }

    fun signOut() {
        auth.signOut()
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}