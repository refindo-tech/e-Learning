package com.example.kessekolah.model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.kessekolah.data.database.MateriData
import com.example.kessekolah.data.repo.MateriRepository

class DetailMateriViewModel(application: Application) : ViewModel() {

    private val materiBookMarkRepository: MateriRepository = MateriRepository(application)

    fun getFavoriteData(): LiveData<List<MateriData>> =
        materiBookMarkRepository.getAllFavorite()
}