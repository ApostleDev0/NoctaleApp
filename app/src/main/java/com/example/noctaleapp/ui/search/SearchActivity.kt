package com.example.your_app_package // IMPORTANT: Replace with your actual package name

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchActivity : AppCompatActivity() {

    private lateinit var searchEditText: EditText
    private lateinit var chipGroupHistory: ChipGroup
    private lateinit var clearAllButton: Button
    private lateinit var bottomNavigationView: BottomNavigationView

    private lateinit var sharedPreferences: SharedPreferences
    private val PREFS_NAME = "search_history_prefs"
    private val KEY_SEARCH_HISTORY = "search_history"
    private val MAX_HISTORY_ITEMS = 10 // Limit the number of history items

    private var searchHistory: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search) // Make sure this matches your XML file name

        // Initialize views
        searchEditText = findViewById(R.id.edit_text_search)
        chipGroupHistory = findViewById(R.id.chip_group_history)
        clearAllButton = findViewById(R.id.button_clear_all)
        bottomNavigationView = findViewById(R.id.bottom_navigation_view)

        // Initialize SharedPreferences for persistent history
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Load search history when activity starts
        loadSearchHistory()

        // Set up search input listener
        searchEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                val query = searchEditText.text.toString().trim()
                if (query.isNotEmpty()) {
                    addSearchQueryToHistory(query)
                    performSearch(query)
                    searchEditText.text.clear() // Clear input after search
                }
                true // Consume the event
            } else {
                false
            }
        }

        // Set up clear all history button listener
        clearAllButton.setOnClickListener {
            clearSearchHistory()
        }

        // Set up bottom navigation listener
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "Navigated to Home", Toast.LENGTH_SHORT).show()
                    // Handle navigation to Home Activity/Fragment
                    true
                }
                R.id.nav_library -> {
                    Toast.makeText(this, "Navigated to Library", Toast.LENGTH_SHORT).show()
                    // Handle navigation to Library Activity/Fragment
                    true
                }
                R.id.nav_search -> {
                    Toast.makeText(this, "Navigated to Search", Toast.LENGTH_SHORT).show()
                    // This is the current screen, so no actual navigation needed
                    true
                }
                R.id.nav_account -> {
                    Toast.makeText(this, "Navigated to Account", Toast.LENGTH_SHORT).show()
                    // Handle navigation to Account Activity/Fragment
                    true
                }
                else -> false
            }
        }

        // Set the "Tìm kiếm" item as selected by default
        bottomNavigationView.selectedItemId = R.id.nav_search
    }

    // --- Search History Management Functions ---

    private fun loadSearchHistory() {
        val json = sharedPreferences.getString(KEY_SEARCH_HISTORY, null)
        if (json != null) {
            val type = object : TypeToken<MutableList<String>>() {}.type
            searchHistory = Gson().fromJson(json, type) ?: mutableListOf()
        }
        renderSearchHistoryChips()
    }

    private fun saveSearchHistory() {
        val json = Gson().toJson(searchHistory)
        sharedPreferences.edit().putString(KEY_SEARCH_HISTORY, json).apply()
    }

    private fun addSearchQueryToHistory(query: String) {
        // Remove existing entry if it's already in history to move it to the top
        searchHistory.remove(query)
        // Add to the beginning of the list (most recent)
        searchHistory.add(0, query)
        // Limit history size
        if (searchHistory.size > MAX_HISTORY_ITEMS) {
            searchHistory = searchHistory.take(MAX_HISTORY_ITEMS).toMutableList()
        }
        saveSearchHistory()
        renderSearchHistoryChips()
    }

    private fun removeSearchQueryFromHistory(query: String) {
        searchHistory.remove(query)
        saveSearchHistory()
        renderSearchHistoryChips()
    }

    private fun clearSearchHistory() {
        searchHistory.clear()
        saveSearchHistory()
        renderSearchHistoryChips()
        Toast.makeText(this, "Lịch sử tìm kiếm đã được xoá", Toast.LENGTH_SHORT).show()
    }

    private fun renderSearchHistoryChips() {
        chipGroupHistory.removeAllViews() // Clear existing chips

        searchHistory.forEach { query ->
            val chip = Chip(this).apply {
                text = query
                isCloseIconVisible = true // Show the 'X' icon
                isClickable = true
                isFocusable = true
                // Set chip style (optional, requires Material Components theme)
                // style = R.style.Widget_MaterialComponents_Chip_Action
                setOnCloseIconClickListener {
                    removeSearchQueryFromHistory(query)
                }
                setOnClickListener {
                    // When a chip is clicked, perform the search again
                    searchEditText.setText(query) // Set the query back to the search bar
                    performSearch(query)
                    addSearchQueryToHistory(query) // Re-add to history to move to top
                }
            }
            chipGroupHistory.addView(chip)
        }
    }

    // --- Placeholder for actual search logic ---
    private fun performSearch(query: String) {
        Toast.makeText(this, "Searching for: $query", Toast.LENGTH_SHORT).show()
        // In a real app, you would start a new activity with search results or update a RecyclerView
        // val intent = Intent(this, SearchResultsActivity::class.java)
        // intent.putExtra("query", query)
        // startActivity(intent)
    }
}