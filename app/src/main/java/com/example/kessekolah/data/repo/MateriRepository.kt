package com.example.kessekolah.data.repo

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.kessekolah.data.database.DatabaseProvider
import com.example.kessekolah.data.database.MateriBookMarkDao
import com.example.kessekolah.data.database.MateriData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.Calendar
import java.util.UUID
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.coroutines.resume

class MateriRepository(application: Application) {

    private val materiRef = FirebaseDatabase.getInstance().getReference("materi")
    private val _materiList = MutableLiveData<List<MateriData>>()
    val materiList: LiveData<List<MateriData>> = _materiList
    private val storage = FirebaseStorage.getInstance().reference
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading
    private val materiBookMarkDao: MateriBookMarkDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    fun setLoading(isLoading: Boolean) {
        _loading.value = isLoading
    }

    init {
        fetchMateriList()
        val db = DatabaseProvider.getDatabase(application)
        materiBookMarkDao = db.materiBookMarkDao()
    }

    fun fetchMateriList() {
        setLoading(true)
        materiRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = ArrayList<MateriData>()
                for (fileSnapshot in snapshot.children) {
                    val id = fileSnapshot.child("id").getValue(Int::class.java) ?: 0
                    val fileName = fileSnapshot.child("fileName").getValue(String::class.java) ?: ""
                    val judul = fileSnapshot.child("judul").getValue(String::class.java) ?: ""
                    val timeStamp = fileSnapshot.child("timestamp").getValue(String::class.java) ?: ""
                    val tahun = fileSnapshot.child("tahun").getValue(String::class.java) ?: ""
                    val fileUrl = fileSnapshot.child("fileUrl").getValue(String::class.java) ?: ""
                    val dataIcon = fileSnapshot.child("dataIlus").getValue(Int::class.java) ?: 0
                    val backColorBanner = fileSnapshot.child("backColorBanner").getValue(String::class.java) ?: ""
                    val materi = MateriData(
                        id = id,
                        judul = judul,
                        tahun = tahun,
                        category = "Materi",
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
                setLoading(false)
            }

            override fun onCancelled(error: DatabaseError) {
                setLoading(false)
                Log.e("ListMateriFragment", "Failed to read database", error.toException())
            }
        })
    }

    suspend fun uploadMateri(id: Int,judul: String, tahun: String, file: File, numberIlus: Int, uid: String, backColorBanner: String) {
        val fileId = UUID.randomUUID().toString()
        val fileRef = storage.child("materi/$uid/$fileId.pdf")

        val metadata = StorageMetadata.Builder()
            .setCustomMetadata("owner", uid)
            .build()

        val uri = Uri.fromFile(file)
        fileRef.putFile(uri, metadata).await()
        val downloadUrl = fileRef.downloadUrl.await()

        val materiData = MateriData(
            id = id,
            judul = judul,
            tahun = tahun,
            category = "Materi",
            fileName = "$fileId.pdf",
            fileUrl = downloadUrl.toString(),
            timestamp = getCurrentDateTime(),
            uid = uid,
            dataIlus = numberIlus,
            backColorBanner = backColorBanner
        )

        materiRef.child(fileId).setValue(materiData).await()
    }

    fun deleteMateri(data: MateriData) {
        setLoading(true)
        val materiQuery = materiRef.orderByChild("judul").equalTo(data.judul)

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
                setLoading(false)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                setLoading(false)
                Log.e("ViewModelListMateri", "onCancelled", databaseError.toException())
            }
        })
    }

    suspend fun editMateri(newTitle: String, newYear: String, newIcon: Int, fileName: String): Boolean {
        setLoading(true)
        Log.d("EditMateriViewModel", fileName)
        val updates = mapOf(
            "judul" to newTitle,
            "tahun" to newYear,
            "icon" to newIcon,
        )

        return suspendCancellableCoroutine { continuation ->
            materiRef.child(fileName).updateChildren(updates)
                .addOnSuccessListener {
                    setLoading(false)
                    Log.d("EditMateriViewModel", "Materi berhasil diperbarui.")
                    continuation.resume(false) // Ubah menjadi `resume(false)` jika berhasil
                }
                .addOnFailureListener { e ->
                    setLoading(false)
                    Log.e("EditMateriViewModel", "Gagal memperbarui materi", e)
                    continuation.resume(true) // Ubah menjadi `resume(true)` jika gagal
                }
        }
    }

    fun insertMateriBookMark(newMateri: MateriData) {
        executorService.execute {
            Log.d("MateriRepository", "Adding new materi: $newMateri")
            materiBookMarkDao.insert(newMateri)
        }
    }

    fun deleteMateriBookMark(id: Int) {
        executorService.execute { materiBookMarkDao.delete(id) }
    }

    fun getAllFavorite(): LiveData<List<MateriData>> {
        val favorites = materiBookMarkDao.getAllFavorite()
        Log.d("MateriRepository", "Current favorites: $favorites")
        return favorites
    }

    private fun getCurrentDateTime(): String {
        return Calendar.getInstance().time.toString()
    }
}
