package com.example.kessekolah.ui.core.beranda.forum.listQuestions

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kessekolah.data.database.PostConsultation
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ListQuestionsViewModel : ViewModel() {
    private val questionRef = FirebaseDatabase.getInstance().getReference("tanya ahli")
    private val _questionList = MutableLiveData<List<PostConsultation>>()
    val questionList: LiveData<List<PostConsultation>> = _questionList
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    init {
        fetchQuestionList()
    }

    private fun fetchQuestionList() {
        _loading.value = true
        questionRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<PostConsultation>()
                for (fileSnapshot in snapshot.children) {
                    val question = fileSnapshot.getValue(PostConsultation::class.java)
                    question?.let { list.add(it) }
                }
                _questionList.value = list
                _loading.value = false
            }

            override fun onCancelled(error: DatabaseError) {
                _loading.value = false
                Log.e("ListVideoViewModel", "Failed to read database", error.toException())
            }
        })
    }

    fun deleteQuestion(data: PostConsultation) {
        _loading.value = true
        val videoRefById = questionRef.child(data.guestId)
        videoRefById.removeValue().addOnSuccessListener {
            Log.d("ListVideoViewModel", "File deleted successfully")
            _loading.value = false
        }.addOnFailureListener {
            Log.e("ListVideoViewModel", "Error deleting file", it)
            _loading.value = false
        }
    }
}