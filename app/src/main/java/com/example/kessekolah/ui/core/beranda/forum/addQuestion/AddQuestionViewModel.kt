package com.example.kessekolah.ui.core.beranda.forum.addQuestion

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kessekolah.data.database.PostConsultation
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storageMetadata

class AddQuestionViewModel : ViewModel() {
    private val databaseRef = FirebaseDatabase.getInstance().getReference("ask_expert")
    private val storage = FirebaseStorage.getInstance().reference
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading
    private val _messageText = MutableLiveData<String>()
    val messageText: LiveData<String> = _messageText

    fun addQuestion(guestId: String, name: String, question: String, description: String, timestamp: String, imageUri: Uri?) {
        _loading.value = true
        if (imageUri != null) {
            val fileRef = storage.child("ask_expert/$guestId/$guestId")
            val metadata = storageMetadata {
                setCustomMetadata("owner", guestId)
            }
            fileRef.putFile(imageUri, metadata).addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener{ document ->
                    val userQuestion = PostConsultation(
                        guestId = guestId,
                        name = name,
                        question = question,
                        description = description,
                        timestamp = timestamp,
                        imgURL = document.toString()
                    )
                    databaseRef.child(guestId).setValue(userQuestion).addOnSuccessListener {
                        _loading.value = false
                        _messageText.value = "successfully sent question"
                    }
                        .addOnFailureListener {
                            _loading.value = false
                            _messageText.value = "failed to send question"
                        }
                }
            }
        } else {
            val userQuestion = PostConsultation(
                guestId = guestId,
                name = name,
                question = question,
                description = description,
                timestamp = timestamp
            )
            databaseRef.child(guestId).setValue(userQuestion).addOnSuccessListener {
                _loading.value = false
                _messageText.value = "successfully sent question"
            }
                .addOnFailureListener {
                    _loading.value = false
                    _messageText.value = "failed to send question"
                }
        }
    }
}