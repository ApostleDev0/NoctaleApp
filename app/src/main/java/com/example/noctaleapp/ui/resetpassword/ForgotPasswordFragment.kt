package com.example.noctaleapp.ui.resetpassword

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.noctaleapp.R

class ForgotPasswordFragment : Fragment() {
    
    // Định nghĩa interface và quy tắc xử lý mũi tên
    interface OnForgotPasswordListener {
        fun onBackPassword() // quy tắc
    }

    // tạo biến để lưu
    private var listener: OnForgotPasswordListener? = null

    // lúc fragment được gắn vào Activity, kiểm tra xem Activity đã bấm chưa
    // context ở đây là ResetPasswordActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnForgotPasswordListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnForgotPasswordListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    // Xử lý khi bấm mũi tên
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val backPassword = view.findViewById<ImageView>(R.id.btn_back_password)

        // gán sự kiện bấm cho mũi tên
        backPassword.setOnClickListener {
            listener?.onBackPassword()
        }
    }

    // khi fragment không còn gắn trong Activity nữa thì gỡ
    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}