package com.example.noctaleapp.ui

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

    interface ReaderSettingsListener {
        fun onSettingsApplied(fontSize: Int, textColorResId: Int, bgColorResId: Int)
    }

    private var listener: ReaderSettingsListener? = null

    private lateinit var seekBarFontSize: SeekBar
    private lateinit var btnBgWhite: Button
    private lateinit var btnBgSepia: Button
    private lateinit var btnBgDark: Button
    private lateinit var btnTextBlack: Button
    private lateinit var btnTextGray: Button
    private lateinit var btnTextWhite: Button
    private lateinit var buttonApplySettings: Button

    private var selectedFontSize: Int = 18
    private var selectedTextColorResId: Int = R.color.default_text_color
    private var selectedBgColorResId: Int = R.color.default_bg_color

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ReaderSettingsListener) {
            listener = context
        } else {
            Log.w(TAG, "$context must implement ReaderSettingsListener if it wants to react immediately to changes.")
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_reader_settings, null)

        seekBarFontSize = view.findViewById(R.id.seekBarFontSize)
        btnBgWhite = view.findViewById(R.id.btnBgWhite)
        btnBgSepia = view.findViewById(R.id.btnBgSepia)
        btnBgDark = view.findViewById(R.id.btnBgDark)
        btnTextBlack = view.findViewById(R.id.btnTextBlack)
        btnTextGray = view.findViewById(R.id.btnTextGray)
        btnTextWhite = view.findViewById(R.id.btnTextWhite)
        buttonApplySettings = view.findViewById(R.id.buttonApplySettings)

        loadCurrentSettings()
        setupViewListeners()

        builder.setView(view)
        return builder.create()
    }

    private fun loadCurrentSettings() {
        selectedFontSize = ReadingSettingsManager.getFontSize(requireContext())
        selectedTextColorResId = ReadingSettingsManager.getTextColorResId(requireContext())
        selectedBgColorResId = ReadingSettingsManager.getBgColorResId(requireContext())

        seekBarFontSize.progress = selectedFontSize
        // TODO: Cập nhật làm nổi bật nút đang được chọn)
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
        btnTextBlack.setOnClickListener {
            selectedTextColorResId = R.color.reader_text_black
            updateColorButtonStates()
        }
        btnTextGray.setOnClickListener {
            selectedTextColorResId = R.color.reader_text_gray
            updateColorButtonStates()
        }
        btnTextWhite.setOnClickListener {
            selectedTextColorResId = R.color.reader_text_white_standalone
            updateColorButtonStates()
        }


        buttonApplySettings.setOnClickListener {
            ReadingSettingsManager.saveSettings(
                requireContext(),
                selectedFontSize,
                selectedTextColorResId,
                selectedBgColorResId
            )
            listener?.onSettingsApplied(selectedFontSize, selectedTextColorResId, selectedBgColorResId)
            dismiss()
        }
    }

    private fun updateColorButtonStates() {
        Log.d(TAG, "Updating button states: TextColor=$selectedTextColorResId, BgColor=$selectedBgColorResId")
    }


    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    companion object {
        const val TAG = "ReaderSettingsDialog"
        fun newInstance(): ReaderSettingsDialogFragment {
            return ReaderSettingsDialogFragment()
        }
    }
}