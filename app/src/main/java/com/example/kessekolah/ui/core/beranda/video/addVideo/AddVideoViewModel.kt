package com.example.kessekolah.ui.core.beranda.video.addVideo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kessekolah.data.database.VideoList
import com.google.firebase.database.FirebaseDatabase

class AddVideoViewModel : ViewModel() {
    private val videoRef = FirebaseDatabase.getInstance().getReference("video_lessons")
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading
    private val _messageText = MutableLiveData<String>()
    val messageText: LiveData<String> = _messageText

    fun addVideo(
        videoId: String,
        uid: String,
        judul: String,
        category: String,
        videoUrl: String,
        timestamp: String,
        dataIlus: Int,
        backColorBanner: String
    ) {
        _loading.value = true

        val video = VideoList(
            videoId = videoId,
            uid = uid,
            judul = judul,
            category = category,
            videoUrl = videoUrl,
            timestamp = timestamp,
            dataIlus = dataIlus,
            backColorBanner = backColorBanner
        )

        // Assume materiRef is initialized correctly in your ViewModel
        videoRef.child(videoId).setValue(video)
            .addOnSuccessListener {
                _loading.value = false
                _messageText.value = "Successfully Added Video"
            }
            .addOnFailureListener {
                _loading.value = false
                _messageText.value = "Failed to Add Video"
            }
    }

}