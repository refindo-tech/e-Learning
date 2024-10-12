package com.example.kessekolah.ui.core.beranda.referensi.listReferensi

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kessekolah.data.database.MateriData
import com.example.kessekolah.data.repo.MateriRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class ListReferensiViewModel : ViewModel() {
    private val materiRef = FirebaseDatabase.getInstance().getReference("referensi")
    private val _materiList = MutableLiveData<List<MateriData>>()
    val materiList: LiveData<List<MateriData>> = _materiList
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    init {
        fetchMateriList()
    }

    private fun fetchMateriList() {
        _loading.value = true
        materiRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = ArrayList<MateriData>()
                for (fileSnapshot in snapshot.children) {
                    val id = fileSnapshot.child("id").getValue(Int::class.java) ?: 0
                    val fileName = fileSnapshot.child("fileName").getValue(String::class.java) ?: ""
                    val judul = fileSnapshot.child("judul").getValue(String::class.java) ?: ""
                    val timeStamp = fileSnapshot.child("timestamp").getValue(String::class.java) ?: ""
                    val tahun = fileSnapshot.child("tahun").getValue(String::class.java) ?: ""
                    val category = fileSnapshot.child("category").getValue(String::class.java) ?: ""
                    val uid = fileSnapshot.child("uid").getValue(String::class.java) ?: ""
                    val fileUrl = fileSnapshot.child("fileUrl").getValue(String::class.java) ?: ""
                    val backColorBanner = fileSnapshot.child("backColorBanner").getValue(String::class.java) ?: ""
                    val dataIcon = fileSnapshot.child("dataIlus").getValue(Int::class.java) ?: 0
                    val materi = MateriData(
                        id = id,
                        judul = judul,
                        tahun = tahun,
                        category = category,
                        fileName = fileName,
                        fileUrl = fileUrl,
                        timestamp = timeStamp,
                        uid = uid,
                        dataIlus = dataIcon,
                        backColorBanner = backColorBanner
                    )
                    list.add(materi)
                }
                _materiList.value = list
                _loading.value = false
            }

            override fun onCancelled(error: DatabaseError) {
                _loading.value = false
                Log.e("ListMateriFragment", "Failed to read database", error.toException())
            }
        })
    }

    fun deleteMateri(data: MateriData) {
        _loading.value = true
        val materiQuery: Query = materiRef.orderByChild("judul").equalTo(data.judul)

        materiQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (materiSnapshot in dataSnapshot.children) {
                    val fileUrl = materiSnapshot.child("fileUrl").getValue(String::class.java)
                    materiSnapshot.ref.removeValue()

                    fileUrl?.let {
                        val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(it)
                        storageRef.delete().addOnSuccessListener {

                            Log.d("ViewModelListMateri", "File deleted successfully")
                        }.addOnFailureListener { exception ->

                            Log.e("ViewModelListMateri", "Error deleting file", exception)
                        }
                    }
                }
                _loading.value = false
            }

            override fun onCancelled(databaseError: DatabaseError) {
                _loading.value = false
                Log.e("ViewModelListMateri", "onCancelled", databaseError.toException())
            }
        })
    }
}