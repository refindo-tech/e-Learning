package com.example.kessekolah.ui.core.beranda.video.listVideo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kessekolah.data.database.VideoList
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ListVideoViewModel : ViewModel() {
    private val videoRef = FirebaseDatabase.getInstance().getReference("video_lessons")
    private val _videoList = MutableLiveData<List<VideoList>>()
    val videoList: LiveData<List<VideoList>> = _videoList
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    init {
        fetchVideoList()
    }

    private fun fetchVideoList() {
        _loading.value = true
        videoRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<VideoList>()
                for (fileSnapshot in snapshot.children) {
                    val video = fileSnapshot.getValue(VideoList::class.java)
                    video?.let { list.add(it) }
                }
                _videoList.value = list
                _loading.value = false
            }

            override fun onCancelled(error: DatabaseError) {
                _loading.value = false
                Log.e("ListVideoViewModel", "Failed to read database", error.toException())
            }
        })
    }

    fun deleteVideo(data: VideoList) {
        _loading.value = true
        val videoRefById = videoRef.child(data.videoId) // pastikan videoId adalah kunci unik di Firebase
        videoRefById.removeValue().addOnSuccessListener {
            Log.d("ListVideoViewModel", "File deleted successfully")
            _loading.value = false
        }.addOnFailureListener {
            Log.e("ListVideoViewModel", "Error deleting file", it)
            _loading.value = false
        }
    }
}