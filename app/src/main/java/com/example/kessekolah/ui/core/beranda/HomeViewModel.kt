package com.example.kessekolah.ui.core.beranda

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kessekolah.data.database.MateriData
import com.example.kessekolah.data.database.VideoList
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeViewModel : ViewModel() {
    private val materiRef = FirebaseDatabase.getInstance().getReference("materi")
    private val videoRef = FirebaseDatabase.getInstance().getReference("video")
    private val _materiList = MutableLiveData<List<MateriData>>()
    val materiList: LiveData<List<MateriData>> = _materiList
    private val _traditionalMedicineList = MutableLiveData<List<MateriData>>()
    val traditionalMedicineList: LiveData<List<MateriData>> = _traditionalMedicineList
    private val _videoList = MutableLiveData<List<VideoList>>()
    val videoList: LiveData<List<VideoList>> = _videoList
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    init {
        fetchMateriList()
        fetchTraditionalMedicineList()
        fetchVideoList()
    }

    private fun fetchMateriList() {
        _loading.value = true
        materiRef.limitToFirst(5).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = ArrayList<MateriData>()
                for (fileSnapshot in snapshot.children) {
                    val id = fileSnapshot.child("id").getValue(Int::class.java) ?: 0
                    val fileName = fileSnapshot.child("fileName").getValue(String::class.java) ?: ""
                    val judul = fileSnapshot.child("judul").getValue(String::class.java) ?: ""
                    val timeStamp =
                        fileSnapshot.child("timestamp").getValue(String::class.java) ?: ""
                    val tahun = fileSnapshot.child("tahun").getValue(String::class.java) ?: ""
                    val fileUrl = fileSnapshot.child("fileUrl").getValue(String::class.java) ?: ""
                    val kategori = fileSnapshot.child("category").getValue(String::class.java) ?: ""
                    val backColorBanner = fileSnapshot.child("backColorBanner").getValue(String::class.java) ?: ""
                    val dataIcon = fileSnapshot.child("dataIlus").getValue(Int::class.java) ?: 0
                    val materi = MateriData(
                        id = id,
                        judul = judul,
                        tahun = tahun,
                        category = kategori,
                        fileName = fileName,
                        fileUrl = fileUrl,
                        timestamp = timeStamp,
                        uid = "",
                        dataIlus = dataIcon,
                        backColorBanner = backColorBanner
                    )
                    list.add(materi)
                }
                _materiList.value = list
                _loading.value = false
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeFragmentViewModel", "Failed to read database", error.toException())
            }
        })
    }

    private fun fetchTraditionalMedicineList() {
        _loading.value = true
        materiRef.orderByChild("category").equalTo("Pengobatan Tradisional").limitToFirst(5)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = ArrayList<MateriData>()
                    for (fileSnapshot in snapshot.children) {
                        val id = fileSnapshot.child("id").getValue(Int::class.java) ?: 0
                        val fileName = fileSnapshot.child("fileName").getValue(String::class.java) ?: ""
                        val judul = fileSnapshot.child("judul").getValue(String::class.java) ?: ""
                        val timeStamp =
                            fileSnapshot.child("timestamp").getValue(String::class.java) ?: ""
                        val tahun = fileSnapshot.child("tahun").getValue(String::class.java) ?: ""
                        val fileUrl = fileSnapshot.child("fileUrl").getValue(String::class.java) ?: ""
                        val kategori = fileSnapshot.child("category").getValue(String::class.java) ?: ""
                        val backColorBanner = fileSnapshot.child("backColorBanner").getValue(String::class.java) ?: ""
                        val dataIcon = fileSnapshot.child("dataIlus").getValue(Int::class.java) ?: 0
                        val materi = MateriData(
                            id = id,
                            judul = judul,
                            tahun = tahun,
                            category = kategori,
                            fileName = fileName,
                            fileUrl = fileUrl,
                            timestamp = timeStamp,
                            uid = "",
                            dataIlus = dataIcon,
                            backColorBanner = backColorBanner
                        )
                        list.add(materi)
                    }
                    _traditionalMedicineList.value = list
                    _loading.value = false
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("HomeFragmentViewModel", "Failed to read database", error.toException())
                }
            })
    }

    private fun fetchVideoList() {
        _loading.value = true
        videoRef.limitToFirst(5).addValueEventListener(object : ValueEventListener {
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
}