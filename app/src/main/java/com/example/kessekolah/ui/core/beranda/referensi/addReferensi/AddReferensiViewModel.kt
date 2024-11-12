package com.example.kessekolah.ui.core.beranda.referensi.addReferensi

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddReferensiViewModel : ViewModel() {
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun setLoading(isLoading: Boolean) {
        _loading.value = isLoading
    }
}