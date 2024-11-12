package com.example.kessekolah.ui.core.beranda.referensi.editReferensi

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class EditReferensiViewModel : ViewModel() {
    private val materiRef = FirebaseDatabase.getInstance().getReference("references")
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    suspend fun editMateri(newTitle: String, newYear: String, datailus: Int, fileName: String): Boolean {
        _loading.value = true
        Log.d("EditMateriViewModel", fileName)
        val updates = mapOf(
            "judul" to newTitle,
            "tahun" to newYear,
            "dataIlus" to datailus,
        )

        return suspendCancellableCoroutine { continuation ->
            materiRef.child(fileName).updateChildren(updates)
                .addOnSuccessListener {
                    _loading.value = false
                    Log.d("EditMateriViewModel", "Materi berhasil diperbarui.")
                    continuation.resume(false)
                }
                .addOnFailureListener { e ->
                    _loading.value = false
                    Log.e("EditMateriViewModel", "Gagal memperbarui materi", e)
                    continuation.resume(true)
                }
        }
    }
}