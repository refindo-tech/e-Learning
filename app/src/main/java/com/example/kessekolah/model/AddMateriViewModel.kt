package com.example.kessekolah.ui.core.beranda.materi.addMateri

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddMateriViewModel: ViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun setLoading(isLoading: Boolean) {
        _loading.value = isLoading
    }

}