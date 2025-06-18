package com.example.noctaleapp.ui

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.noctaleapp.R
import com.example.noctaleapp.viewmodel.HomeViewModel
import com.example.noctaleapp.BuildConfig
import com.google.firebase.firestore.FirebaseFirestore
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.launch

class EditProfileActivity : AppCompatActivity() {
    private lateinit var ReturnButton: ImageButton
    private lateinit var saveButton: TextView
    private lateinit var usernameInput: EditText
    private lateinit var displayNameInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var profileImage: ImageView

    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>
    private var selectedImageUri: Uri? = null

    private val homeViewModel: HomeViewModel by viewModels()

    val supabase = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_ANON_KEY,
    ) {
        install(Storage)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile)
        getLauncher()
        ReturnButton = findViewById(R.id.imgButtonReturn)
        saveButton = findViewById(R.id.saveDataButton)
        usernameInput = findViewById(R.id.usernameInput)
        displayNameInput = findViewById(R.id.displayNameInput)
        descriptionInput = findViewById(R.id.descriptionInput)
        profileImage = findViewById(R.id.profileImage)

        val uid = intent.getStringExtra("uid")
        if (uid.isNullOrEmpty()) {
            Toast.makeText(this, "Không tìm thấy UID người dùng", Toast.LENGTH_SHORT).show()
            finish()
            return
        }


        ReturnButton.setOnClickListener {
            finish()
        }

        profileImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        saveButton.setOnClickListener {
            handleSaveProfile(uid)
        }


        homeViewModel.fetchUserById(uid)
        Log.d("EditProfile", "User UID: $uid")
        Log.d("EditProfile", "User received: ${homeViewModel.users.value}")
        renderInputData()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun renderInputData() {
        homeViewModel.users.observe(this) { user ->
            Log.d("EditProfile", "User received: $user")
            usernameInput.setText(user.username)
            displayNameInput.setText(user.displayName)
            descriptionInput.setText(user.description)
            Glide.with(this)
                .load(user.avatarUrl)
                .into(profileImage)
        }
    }

    private fun getLauncher(){
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                selectedImageUri = it
                findViewById<ImageView>(R.id.profileImage).setImageURI(it)
            }
        }
    }

    private fun getBytesFromUri(context: Context, uri: Uri): ByteArray? {
        val inputStream = context.contentResolver.openInputStream(uri)
        return inputStream?.readBytes() ?: throw IllegalArgumentException("Không thể mở ảnh từ thiết bị.")
    }

    fun handleSaveProfile(uid: String) {
        val uri = selectedImageUri
        if (uri == null) {
            Toast.makeText(this, "Vui lòng chọn ảnh trước khi lưu", Toast.LENGTH_SHORT).show()
        }
        val username = usernameInput.text.toString()
        val displayName = displayNameInput.text.toString()
        val description = descriptionInput.text.toString()

        lifecycleScope.launch {
            try {
                val imageUrl = uploadImageToSupabase(uri!!, uid)

                saveDataToFirestore(
                    uid = uid,
                    username = username,
                    displayName = displayName,
                    description = description,
                    avatarUrl = imageUrl
                )

                Toast.makeText(this@EditProfileActivity, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@EditProfileActivity, "Lỗi: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }

    suspend fun uploadImageToSupabase(uri: Uri, uid: String): String {
        val fileName = "profileuser_$uid.jpg"
        val byteArray = getBytesFromUri(this, uri) ?: throw IllegalArgumentException("Không đọc được ảnh từ thiết bị")

        val bucket = supabase.storage["noctale.profile.image"]
        bucket.upload(
            path = fileName,
            data = byteArray,
            upsert = true
        )

        return "https://osadkjpbnyvrlpptmjcw.supabase.co/storage/v1/object/public/noctale.profile.image/$fileName"
    }

    fun saveDataToFirestore(uid: String, username: String, displayName: String, description: String, avatarUrl: String) {
        val userData = mapOf(
            "username" to username,
            "displayName" to displayName,
            "description" to description,
            "avatarUrl" to avatarUrl
        )
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(uid).set(userData)
    }
}