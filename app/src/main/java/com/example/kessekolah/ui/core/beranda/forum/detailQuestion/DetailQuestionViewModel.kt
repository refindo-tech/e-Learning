package com.example.kessekolah.ui.core.beranda.forum.detailQuestion

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kessekolah.data.database.Answer
import com.example.kessekolah.data.database.PostConsultation
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storageMetadata

class DetailQuestionViewModel : ViewModel() {
    private val databaseRef = FirebaseDatabase.getInstance().getReference("tanya ahli")
    private val _answerList = MutableLiveData<List<Answer>>()
    val answerList: LiveData<List<Answer>> = _answerList
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading
    private val _messageText = MutableLiveData<String>()
    val messageText: LiveData<String> = _messageText


    fun addAnswer(guestId: String, uid: String, answer: Answer) {
        _loading.value = true

        val answerRef = databaseRef.child(guestId).child("answers").child(uid)

        answerRef.setValue(answer)
            .addOnSuccessListener {
                _loading.value = false
                _messageText.value = "Jawaban berhasil dikirim"
            }
            .addOnFailureListener { error ->
                _loading.value = false
                _messageText.value = "Gagal mengirim jawaban: ${error.message}"
            }
    }

    fun fetchAnswerList(guestId: String) {
        databaseRef.child(guestId).child("answers").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Answer>()
                for (answerSnapshot in snapshot.children) {
                    val answer = answerSnapshot.getValue(Answer::class.java)
                    answer?.let { list.add(it) }
                }
                _answerList.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ListVideoViewModel", "Failed to read database", error.toException())
            }
        })
    }
    fun deleteAnswer(guestId: String, uid: String) {
        _loading.value = true
        val answerRef = databaseRef.child(guestId).child("answers").child(uid)
        answerRef.removeValue().addOnSuccessListener {
            Log.d("ListVideoViewModel", "File deleted successfully")
            _loading.value = false
        }.addOnFailureListener {
            Log.e("ListVideoViewModel", "Error deleting file", it)
            _loading.value = false
        }
    }
}