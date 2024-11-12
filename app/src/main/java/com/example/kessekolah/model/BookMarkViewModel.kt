package com.example.kessekolah.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kessekolah.data.database.MateriData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BookMarkViewModel : ViewModel() {

    private val dbUsers = FirebaseDatabase.getInstance().getReference("users")
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _bookmarks = MutableLiveData<List<MateriData>>()
    val bookmarks: LiveData<List<MateriData>> get() = _bookmarks

    fun getAllBookmarks(userId: String): LiveData<List<MateriData>> {
        _isLoading.value = true
        dbUsers.child(userId).child("userBookmarkMateri")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val bookmarkList = mutableListOf<MateriData>()
                    for (bookmarkSnapshot in snapshot.children) {
                        val id = bookmarkSnapshot.child("id").getValue(Int::class.java) ?: 0
                        val fileName = bookmarkSnapshot.child("fileName").getValue(String::class.java) ?: ""
                        val judul = bookmarkSnapshot.child("judul").getValue(String::class.java) ?: ""
                        val timeStamp = bookmarkSnapshot.child("timestamp").getValue(String::class.java) ?: ""
                        val tahun = bookmarkSnapshot.child("tahun").getValue(String::class.java) ?: ""
                        val uid = bookmarkSnapshot.child("uid").getValue(String::class.java) ?: ""
                        val category = bookmarkSnapshot.child("category").getValue(String::class.java) ?: ""
                        val fileUrl = bookmarkSnapshot.child("fileUrl").getValue(String::class.java) ?: ""
                        val backColorBanner = bookmarkSnapshot.child("backColorBanner").getValue(String::class.java) ?: ""
                        val dataIcon = bookmarkSnapshot.child("dataIlus").getValue(Int::class.java) ?: 0
                        val materi = MateriData(
                            id = id,
                            uid = uid,
                            judul = judul,
                            tahun = tahun,
                            category = category,
                            fileName = fileName,
                            fileUrl = fileUrl,
                            timestamp = timeStamp,
                            dataIlus = dataIcon,
                            backColorBanner = backColorBanner
                        )
                        bookmarkList.add(materi)
                    }
                    _bookmarks.value = bookmarkList
                    _isLoading.value = false
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("BookMarkViewModel", "Error fetching bookmarks from Firebase Database", error.toException())
                    _isLoading.value = false
                }
            })
        return bookmarks
    }

//    fun addBookmarkToFirebase(userId: String, materiData: MateriData) {
//        val materiBookmark = hashMapOf(
//            "judul" to materiData.judul,
//            "category" to materiData.category,
//            "timestamp" to materiData.timestamp,
//            "fileUrl" to materiData.fileUrl
//        )
//
//        dbUsers.child(userId).child("userBookmarkMateri").push()
//            .setValue(materiBookmark)
//            .addOnSuccessListener {
//                Log.d("BookMarkViewModel", "Bookmark added to Firebase Database")
//            }
//            .addOnFailureListener { e ->
//                Log.e("BookMarkViewModel", "Error adding bookmark to Firebase Database", e)
//            }
//    }

    fun removeBookmarkFromFirebase(userId: String, judul: String): LiveData<List<MateriData>> {
        dbUsers.child(userId).child("userBookmarkMateri")
            .orderByChild("judul").equalTo(judul)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (bookmarkSnapshot in snapshot.children) {
                        bookmarkSnapshot.ref.removeValue()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("BookMarkViewModel", "Error removing bookmark from Firebase Database", error.toException())
                }
            })
        return bookmarks
    }
}
