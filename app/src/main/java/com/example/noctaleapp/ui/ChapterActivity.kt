package com.example.noctaleapp.ui // Hoặc package của bạn

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.view.View
import android.widget.ImageButton
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.noctaleapp.R
import com.example.noctaleapp.model.Chapter
import com.example.noctaleapp.repository.ChapterRepository
import com.example.noctaleapp.viewmodel.ChapterViewModel
import com.example.noctaleapp.viewmodel.ChapterViewModelFactory
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import kotlin.text.isNotBlank
import android.content.res.Resources
import android.util.Log
import androidx.core.content.ContextCompat

class ChapterActivity : AppCompatActivity(),ReaderSettingsDialogFragment.ReaderSettingsListener {

    // Views từ activity_chapter.xml
    private lateinit var scrollViewReaderContent: ScrollView
    private lateinit var textViewReaderContent: TextView
    private lateinit var readerHomeBtn: ImageButton
    private lateinit var readerSettingsBtn: ImageButton
    private lateinit var readerCommentsBtn: ImageButton
    private lateinit var readerChaptersBtn: ImageButton
    private lateinit var selectChapterLauncher: ActivityResultLauncher<Intent>
    // private lateinit var progressBarChapterLoading: ProgressBar // Bỏ comment nếu bạn thêm ProgressBar

    private lateinit var chapterViewModel: ChapterViewModel

    private var currentBookId: String? = null
    private var currentChapterId: String? = null
    // Bạn có thể không cần lưu trữ trực tiếp next/previous ID ở đây nữa
    // vì ViewModel đã quản lý và Activity sẽ phản ứng với thay đổi từ ViewModel

    companion object {
        const val EXTRA_BOOK_ID = "com.example.noctaleapp.ui.BOOK_ID"
        const val EXTRA_CHAPTER_ID = "com.example.noctaleapp.ui.CHAPTER_ID"
        // Có thể thêm một extra để điều hướng đến chương cụ thể từ danh sách
        // const val EXTRA_TARGET_CHAPTER_ID = "com.example.noctaleapp.ui.TARGET_CHAPTER_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chapter)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        applyReadingSettings()

        // Khởi tạo ViewModel
        val chapterRepository = ChapterRepository() // Khởi tạo ChapterRepository
        val viewModelFactory = ChapterViewModelFactory(chapterRepository)
        chapterViewModel = ViewModelProvider(this, viewModelFactory)[ChapterViewModel::class.java]

        currentBookId = intent.getStringExtra(EXTRA_BOOK_ID)
        currentChapterId = intent.getStringExtra(EXTRA_CHAPTER_ID)
        selectChapterLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result -> // 'result' ở đây là một đối tượng ActivityResult
            if (result.resultCode == RESULT_OK) {
                // Lấy dữ liệu từ Intent trả về
                result.data?.getStringExtra(ReaderPanelActivity.RESULT_SELECTED_CHAPTER_ID)?.let { selectedChapterId ->
                    navigateToChapter(selectedChapterId)
                }
            }
        }
        if (currentBookId != null && currentChapterId != null) {
            chapterViewModel.loadChapter(currentBookId!!, currentChapterId!!)
        } else {
            Toast.makeText(this, "Không có thông tin sách hoặc chương để tải.", Toast.LENGTH_LONG).show()
            finish() // Đóng activity nếu không có ID
        }

        observeViewModel()
        setupBottomMenuListeners()
    }

    private fun initViews() {
        scrollViewReaderContent = findViewById(R.id.scrollViewReaderContent)
        textViewReaderContent = findViewById(R.id.textViewReaderContent)
        readerHomeBtn = findViewById(R.id.ReaderHomeBtn)
        readerSettingsBtn = findViewById(R.id.ReaderSettingsBtn)
        readerCommentsBtn = findViewById(R.id.ReaderCommentsBtn)
        readerChaptersBtn = findViewById(R.id.ReaderChaptersBtn)
        // progressBarChapterLoading = findViewById(R.id.progressBarChapterLoading) // Bỏ comment nếu bạn thêm ProgressBar
    }

    private fun observeViewModel() {
        chapterViewModel.isLoading.observe(this) { isLoading ->
            // progressBarChapterLoading.visibility = if (isLoading) View.VISIBLE else View.GONE // Nếu có ProgressBar
            // Ẩn/hiện nội dung để người dùng biết đang tải
            scrollViewReaderContent.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
            // Có thể muốn vô hiệu hóa các nút menu khi đang tải
            readerHomeBtn.isEnabled = !isLoading
            readerSettingsBtn.isEnabled = !isLoading
            readerCommentsBtn.isEnabled = !isLoading
            readerChaptersBtn.isEnabled = !isLoading
        }

        chapterViewModel.chapterDetails.observe(this) { chapter ->
            if (chapter != null) {
                displayChapterContent(chapter)
                // Cập nhật currentChapterId để phản ánh chương đang hiển thị thực sự
                this.currentChapterId = chapter.id
            } else {
                // Xử lý khi không tải được chương (ngoài thông báo lỗi từ LiveData error)
                // textViewReaderContent.text = getString(R.string.error_loading_chapter_content)
                // Thông báo lỗi đã được xử lý qua chapterViewModel.error
            }
        }

        chapterViewModel.error.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                chapterViewModel.clearErrorMessage() // Xóa lỗi sau khi hiển thị
            }
        }

        // Không cần observe trực tiếp previousChapterId và nextChapterId ở đây
        // nếu bạn không cập nhật UI đặc biệt cho chúng (ví dụ: bật/tắt nút chương trước/sau)
        // Logic điều hướng sẽ gọi loadChapter với ID mới.
    }

    private fun displayChapterContent(chapter: Chapter) {
        // Ví dụ: hiển thị tiêu đề chương ở đầu nội dung
        val titleHtml = "<h1>${chapter.mainTitle}</h1><hr>"
        val contentWithTitle = titleHtml + chapter.content // Giả sử chapter.content là HTML

        val formattedContent: Spanned = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Html.fromHtml(contentWithTitle, Html.FROM_HTML_MODE_LEGACY)
        } else {
            @Suppress("DEPRECATION") // Html.fromHtml(String) is deprecated in API 24
            Html.fromHtml(contentWithTitle)
        }
        textViewReaderContent.text = formattedContent

        // Cuộn lên đầu khi tải chương mới
        scrollViewReaderContent.post {
            scrollViewReaderContent.smoothScrollTo(0, 0)
        }
        applyCurrentSettingsToTextView()
    }

    private fun setupBottomMenuListeners() {
        readerHomeBtn.setOnClickListener {
            // Quay về BookActivity với bookId hiện tại
            currentBookId?.let { bookId ->
                // Giả sử BookActivity là màn hình chi tiết sách và có thể nhận EXTRA_BOOK_ID
                val intent = Intent(this, BookActivity::class.java).apply {
                    putExtra(BookActivity.EXTRA_BOOK_ID, bookId) // Thay BookActivity.EXTRA_BOOK_ID bằng const thực tế
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                }
                startActivity(intent)
                finish() // Đóng ChapterActivity
            } ?: Toast.makeText(this, "Không tìm thấy thông tin sách.", Toast.LENGTH_SHORT).show()
        }

        readerSettingsBtn.setOnClickListener {
            // Mở dialog cài đặt
            val settingsDialog = ReaderSettingsDialogFragment.newInstance()
            // settingsDialog.setReaderSettingsListener(this) // Không cần nếu đã làm trong onAttach
            settingsDialog.show(supportFragmentManager, ReaderSettingsDialogFragment.TAG)
        }

        readerCommentsBtn.setOnClickListener {
            val currentChapter = chapterViewModel.chapterDetails.value // Lấy chương hiện tại từ LiveData
            if (currentChapter == null) {
                Toast.makeText(this, "Thông tin chương hiện tại chưa tải xong.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Giả sử Chapter model của bạn có trường nextChapterId
            val nextChapId = currentChapter.nextChapterId

            if (nextChapId != null && nextChapId.isNotBlank()) {
                // Kiểm tra currentBookId (nên luôn có ở đây nếu chương đã tải)
                currentBookId?.let { bookId ->
                    Toast.makeText(this, "Đang chuyển đến chương tiếp theo...", Toast.LENGTH_SHORT).show()
                    chapterViewModel.loadChapter(bookId, nextChapId)
                } ?: Toast.makeText(this, "Lỗi: Không tìm thấy Book ID.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Bạn đang ở chương cuối cùng.", Toast.LENGTH_SHORT).show()
            }
        }

        readerChaptersBtn.setOnClickListener {
            val chapterIdForIntent = this.currentChapterId

            currentBookId?.let { bookId ->
                val intent = Intent(this, ReaderPanelActivity::class.java).apply {
                    putExtra(ReaderPanelActivity.EXTRA_BOOK_ID, bookId)
                    putExtra(ReaderPanelActivity.EXTRA_CURRENT_CHAPTER_ID, chapterIdForIntent)
                }
                selectChapterLauncher.launch(intent)
            } ?: Toast.makeText(this, "Không có thông tin sách để mở danh sách chương.", Toast.LENGTH_SHORT).show()
        }
    }
    private fun applyCurrentSettingsToTextView() {
        if (!::textViewReaderContent.isInitialized) return // Kiểm tra nếu view chưa được khởi tạo

        val fontSize = ReadingSettingsManager.getFontSize(this)
        val textColorResId = ReadingSettingsManager.getTextColorResId(this)
        val bgColorResId = ReadingSettingsManager.getBgColorResId(this)

        Log.d("ChapterActivity", "Applying settings to TextView: Font=$fontSize, TextColorResId=$textColorResId, BgColorResId=$bgColorResId")

        textViewReaderContent.textSize = fontSize.toFloat()
        try {
            textViewReaderContent.setTextColor(ContextCompat.getColor(this, textColorResId))
            // Thay đổi màu nền của ScrollView hoặc view cha bao quanh nội dung
            // Hoặc của chính cửa sổ nếu bạn muốn toàn bộ màn hình thay đổi
            scrollViewReaderContent.setBackgroundColor(ContextCompat.getColor(this, bgColorResId))
            // Hoặc: window.decorView.setBackgroundColor(ContextCompat.getColor(this, bgColorResId))

        } catch (e: Resources.NotFoundException) {
            Log.e("ChapterActivity", "Lỗi không tìm thấy resource màu: ${e.message}")
            // Có thể đặt màu mặc định ở đây nếu cần
        }
    }


    // Hàm để load và áp dụng cài đặt ban đầu (gọi trong onCreate)
    private fun applyReadingSettings() {
        applyCurrentSettingsToTextView()
    }

    // Implement phương thức từ ReaderSettingsListener
    override fun onSettingsApplied(fontSize: Int, textColorResId: Int, bgColorResId: Int) {
        Log.d("ChapterActivity", "Settings applied via listener: Font=$fontSize, TextColorResId=$textColorResId, BgColorResId=$bgColorResId")
        // Cài đặt đã được lưu bởi DialogFragment
        // Bây giờ áp dụng chúng ngay lập tức vào UI của ChapterActivity
        applyCurrentSettingsToTextView()
    }

    /**
     * Hàm này được gọi khi người dùng chọn một chương mới từ danh sách chương
     * (ví dụ: từ một ChapterListBottomSheetDialogFragment).
     * Nó sẽ yêu cầu ViewModel tải chương mới.
     * @param newChapterId ID của chương mới cần hiển thị.
     */
    fun navigateToChapter(newChapterId: String) {
        if (newChapterId == currentChapterId) {
            Toast.makeText(this, "Bạn đang ở chương này.", Toast.LENGTH_SHORT).show()
            return
        }
        currentBookId?.let { bookId ->
            // currentChapterId sẽ được cập nhật trong observeViewModel khi chapterDetails thay đổi
            chapterViewModel.loadChapter(bookId, newChapterId)
        } ?: Toast.makeText(this, "Lỗi: Không tìm thấy Book ID để tải chương mới.", Toast.LENGTH_SHORT).show()
    }
}