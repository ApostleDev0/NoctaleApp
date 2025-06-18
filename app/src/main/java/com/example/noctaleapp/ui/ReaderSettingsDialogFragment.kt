// ReaderSettingsDialogFragment.kt
package com.example.noctaleapp.ui // Hoặc package của bạn

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.noctaleapp.R

class ReaderSettingsDialogFragment : DialogFragment() {

    // Interface để thông báo cho Activity biết cài đặt đã được áp dụng
    // Activity sẽ tự quyết định làm gì với thông tin này (ví dụ: áp dụng ngay, hoặc chỉ lưu)
    interface ReaderSettingsListener {
        fun onSettingsApplied(fontSize: Int, textColorResId: Int, bgColorResId: Int)
    }

    private var listener: ReaderSettingsListener? = null

    // Views từ dialog_reader_settings.xml
    private lateinit var seekBarFontSize: SeekBar
    private lateinit var btnBgWhite: Button
    private lateinit var btnBgSepia: Button
    private lateinit var btnBgDark: Button
    private lateinit var btnTextBlack: Button
    private lateinit var btnTextGray: Button
    private lateinit var btnTextWhite: Button
    private lateinit var buttonApplySettings: Button

    // Các biến tạm để lưu lựa chọn hiện tại trong dialog
    private var selectedFontSize: Int = 18 // Giá trị mặc định
    private var selectedTextColorResId: Int = R.color.default_text_color // Màu chữ mặc định (tạo trong colors.xml)
    private var selectedBgColorResId: Int = R.color.default_bg_color     // Màu nền mặc định (tạo trong colors.xml)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Gán listener nếu Activity host implement nó
        if (context is ReaderSettingsListener) {
            listener = context
        } else {
            // Log cảnh báo nếu Activity không implement listener, nhưng vẫn cho phép dialog hoạt động
            Log.w(TAG, "$context must implement ReaderSettingsListener if it wants to react immediately to changes.")
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_reader_settings, null) // Sử dụng layout của bạn

        // Khởi tạo views
        seekBarFontSize = view.findViewById(R.id.seekBarFontSize)
        btnBgWhite = view.findViewById(R.id.btnBgWhite)
        btnBgSepia = view.findViewById(R.id.btnBgSepia)
        btnBgDark = view.findViewById(R.id.btnBgDark)
        btnTextBlack = view.findViewById(R.id.btnTextBlack)
        btnTextGray = view.findViewById(R.id.btnTextGray)
        btnTextWhite = view.findViewById(R.id.btnTextWhite)
        buttonApplySettings = view.findViewById(R.id.buttonApplySettings)

        loadCurrentSettings() // Tải cài đặt hiện tại để hiển thị trong dialog
        setupViewListeners()    // Thiết lập listener cho các view trong dialog

        builder.setView(view)
        // Không cần setPositiveButton/setNegativeButton nếu đã có nút "Apply" riêng
        return builder.create()
    }

    private fun loadCurrentSettings() {
        // Đọc cài đặt đã lưu từ SharedPreferences
        selectedFontSize = ReadingSettingsManager.getFontSize(requireContext())
        selectedTextColorResId = ReadingSettingsManager.getTextColorResId(requireContext())
        selectedBgColorResId = ReadingSettingsManager.getBgColorResId(requireContext())

        // Cập nhật UI của dialog
        seekBarFontSize.progress = selectedFontSize
        // TODO: Cập nhật trạng thái của các nút màu (ví dụ: làm nổi bật nút đang được chọn)
        updateColorButtonStates()
    }

    private fun setupViewListeners() {
        seekBarFontSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    selectedFontSize = progress
                    // Có thể thêm preview trực tiếp nếu muốn
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Listeners cho các nút chọn BỘ MÀU NỀN + CHỮ
        // (Giả sử các nút này chọn một cặp màu nền và chữ tương ứng)
        btnBgWhite.setOnClickListener {
            selectedBgColorResId = R.color.reader_bg_white // Tạo các màu này trong colors.xml
            selectedTextColorResId = R.color.reader_text_on_white
            updateColorButtonStates()
        }
        btnBgSepia.setOnClickListener {
            selectedBgColorResId = R.color.reader_bg_sepia
            selectedTextColorResId = R.color.reader_text_on_sepia
            updateColorButtonStates()
        }
        btnBgDark.setOnClickListener {
            selectedBgColorResId = R.color.reader_bg_dark
            selectedTextColorResId = R.color.reader_text_on_dark
            updateColorButtonStates()
        }

        // Listeners cho các nút chọn MÀU CHỮ RIÊNG LẺ
        // (Nếu bạn muốn người dùng có thể tùy chỉnh màu chữ độc lập với các bộ màu ở trên)
        // Lưu ý: Logic này có thể phức tạp hơn nếu bạn cho phép cả hai.
        // Hiện tại, layout của bạn có vẻ như tách biệt chúng.
        btnTextBlack.setOnClickListener {
            selectedTextColorResId = R.color.reader_text_black // Màu chữ đen
            updateColorButtonStates()
        }
        btnTextGray.setOnClickListener {
            selectedTextColorResId = R.color.reader_text_gray // Màu chữ xám
            updateColorButtonStates()
        }
        btnTextWhite.setOnClickListener {
            selectedTextColorResId = R.color.reader_text_white_standalone // Màu chữ trắng (dùng cho nền tối)
            updateColorButtonStates()
        }


        buttonApplySettings.setOnClickListener {
            // Lưu cài đặt mới
            ReadingSettingsManager.saveSettings(
                requireContext(),
                selectedFontSize,
                selectedTextColorResId,
                selectedBgColorResId
            )
            // Thông báo cho listener (Activity) biết rằng cài đặt đã được áp dụng
            listener?.onSettingsApplied(selectedFontSize, selectedTextColorResId, selectedBgColorResId)
            dismiss() // Đóng dialog
        }
    }

    private fun updateColorButtonStates() {
        // TODO: Viết logic để làm nổi bật các nút màu đã chọn.
        // Ví dụ: btnBgWhite.isSelected = (selectedBgColorResId == R.color.reader_bg_white)
        // Bạn có thể cần tạo các drawable selector cho background của các nút để thay đổi
        // giao diện khi isSelected = true.
        Log.d(TAG, "Updating button states: TextColor=$selectedTextColorResId, BgColor=$selectedBgColorResId")
    }


    override fun onDetach() {
        super.onDetach()
        listener = null // Tránh memory leak
    }

    companion object {
        const val TAG = "ReaderSettingsDialog"
        fun newInstance(): ReaderSettingsDialogFragment {
            return ReaderSettingsDialogFragment()
        }
    }
}