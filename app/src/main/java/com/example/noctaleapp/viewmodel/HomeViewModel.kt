package com.example.noctaleapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.noctaleapp.R
import com.example.noctaleapp.model.Book

class HomeViewModel : ViewModel() {
    private val _bookList = MutableLiveData<List<Book>>()
    val bookList: LiveData<List<Book>> = _bookList

    init {
        loadTestData()
    }

    private fun loadTestData() {
        _bookList.value = listOf(
            Book(
                title = "The Alchemist",
                author = "Paulo Coelho",
                rating = 4.5f,
                numberChapter = 10,
                description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec pretium viverra tellus ac lobortis. Morbi sed est lorem.",
                imageUrl = "https://s3-alpha-sig.figma.com/img/11d7/b3f5/f61232cb9c2c8ce7c159c52ce973cbd8?Expires=1748217600&Key-Pair-Id=APKAQ4GOSFWCW27IBOMQ&Signature=NSNvmW8Dbq~fEn9SsnxoLEBtdXRYbQO5PFK95fhBmG-m4WXOvvFHkYhsHba5YE2wmU9Bfb3fxwSKBVx3htnczJgjLlnDmjdhtH7Lxc61ew5W2oIeZULUe-FSHHt4JtXj6yvPrRGl0Ipul~lOxFiEX3Az-9WAg4lGFBgRZxSMLX6wlUBObxiI0d-df37wBmfcMST~V1xM6HquGC6z26PVUlSLfQquO7WgV4FagqC1~wJvGRNDPHkmlt~rv6-4J~GfjbQ9UEjCMsPX3SH2oGJa8~aN3br8UJcXGS0vMDuNS~aor-~RVaOK6XBA16obqNvyVvYgbBi-34YzR8LnU1Qkiw__"
            ),
            Book(
                title = "The Da Vinci Code",
                author = "Dan Brown",
                rating = 3.2f,
                numberChapter = 200,
                description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec pretium viverra tellus ac lobortis. Morbi sed est lorem.",
                localImageRes = R.drawable.bookcover_template
            )
        )
    }
}