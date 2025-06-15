package com.example.noctaleapp.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.noctaleapp.adapter.ProfileTabAdapter
import com.example.noctaleapp.databinding.FragmentProfileBinding
import com.example.noctaleapp.ui.LoginActivity
import com.example.noctaleapp.viewmodel.HomeViewModel
import com.google.android.material.tabs.TabLayoutMediator


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by activityViewModels()

    private val tabTitles = listOf("Product", "Fan", "Follower")

    private lateinit var profileImage: ImageView
    private lateinit var profileName: TextView
    private lateinit var userName: TextView
    private lateinit var profileDescription: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
   }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fetchUserById("HYr8ZD3ycnRBKJIshiK2yuZi99w1")

//        profileImage = binding.profileImage
//        profileName = binding.profileName
//        userName = binding.userName
//        profileDescription = binding.profileDescription
//
//        viewModel.users.observe(viewLifecycleOwner) { user ->
//            profileName.text = user.displayName
////            userName.text = user.uid
//            profileDescription.text = user.description
//            Glide.with(this)
//                .load(user.avatarUrl)
//                .into(profileImage)
//        }

        val adapter = ProfileTabAdapter(this)
        binding.profileViewPage.adapter = adapter
        binding.profileViewPage.isUserInputEnabled = true

        TabLayoutMediator(binding.profileTabLayout, binding.profileViewPage) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        // Lắng nghe sự kiện click từ nút Đăng xuất
        binding.btnLogout.setOnClickListener {
            viewModel.logout() // Gọi hàm logout trong ViewModel
        }

        // Bắt đầu quan sát trạng thái đăng xuất từ ViewModel
        observeLogoutStatus()
    }

    private fun observeLogoutStatus() {
        // Lắng nghe LiveData "logoutComplete"
        viewModel.logoutComplete.observe(viewLifecycleOwner) { hasLoggedOut ->
            // isAdded là một check an toàn, đảm bảo Fragment vẫn đang được gắn vào Activity
            if (hasLoggedOut && isAdded) {
                // Nếu hasLoggedOut là true, tiến hành điều hướng
                goToLoginActivity()
            }
        }
    }
    private fun ProfileFragment.goToLoginActivity() {
        // Sử dụng requireActivity() để lấy context của Activity chứa Fragment
        val intent = Intent(requireActivity(), LoginActivity::class.java)

        // Cờ này sẽ xóa hết các màn hình cũ (như MainActivity)
        // và khởi động LoginActivity như một task mới.
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        // Đóng Activity hiện tại (MainActivity) lại.
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

