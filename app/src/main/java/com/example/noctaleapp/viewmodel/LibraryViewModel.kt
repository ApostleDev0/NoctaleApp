package com.example.noctaleapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.noctaleapp.model.LibraryBook
import com.example.noctaleapp.model.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class LibraryViewModel : ViewModel() {
    private val _libraryBook = MutableLiveData<User>()
    val libraryBook: LiveData<User> = _libraryBook
    fun getLibraryBooks(uid: String): LiveData<List<LibraryBook>> {
        val liveData = MutableLiveData<List<LibraryBook>>()

        Firebase.firestore.collection("users").document(uid)
            .collection("libraryBooks").addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(LibraryBook::class.java)?.copy(id = doc.id)
                    }
                    liveData.value = list
                }
            }
            return liveData
    }

    fun removeBookFromLibrary(uid: String, bookId: String) {
        Log.d("DELETE_BOOK", "UID: $uid | BookID: $bookId")
        FirebaseFirestore.getInstance().collection("users").document(uid)
            .collection("libraryBooks").document(bookId).delete()
    }
}