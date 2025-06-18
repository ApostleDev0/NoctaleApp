package com.example.noctaleapp.ui

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.noctaleapp.R // Thay bằng package R của bạn

object ReadingSettingsManager {
    private const val PREFS_NAME = "NoctaleReaderSettings"
    private const val KEY_FONT_SIZE = "fontSize"
    private const val KEY_TEXT_COLOR_RES_ID = "textColorResId"
    private const val KEY_BG_COLOR_RES_ID = "bgColorResId"

    private const val DEFAULT_FONT_SIZE = 18
    private val DEFAULT_TEXT_COLOR_RES_ID = R.color.default_text_color
    private val DEFAULT_BG_COLOR_RES_ID = R.color.default_bg_color

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveSettings(context: Context, fontSize: Int, textColorResId: Int, bgColorResId: Int) {
        val editor = getPreferences(context).edit()
        editor.putInt(KEY_FONT_SIZE, fontSize)
        editor.putInt(KEY_TEXT_COLOR_RES_ID, textColorResId)
        editor.putInt(KEY_BG_COLOR_RES_ID, bgColorResId)
        editor.apply()
        Log.d("ReadingSettingsManager", "Settings saved: Font=$fontSize, TextColor=$textColorResId, BgColor=$bgColorResId")
    }

    fun getFontSize(context: Context): Int {
        return getPreferences(context).getInt(KEY_FONT_SIZE, DEFAULT_FONT_SIZE)
    }

    fun getTextColorResId(context: Context): Int {
        return getPreferences(context).getInt(KEY_TEXT_COLOR_RES_ID, DEFAULT_TEXT_COLOR_RES_ID)
    }

    fun getBgColorResId(context: Context): Int {
        return getPreferences(context).getInt(KEY_BG_COLOR_RES_ID, DEFAULT_BG_COLOR_RES_ID)
    }
}