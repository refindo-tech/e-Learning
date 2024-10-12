package com.example.kessekolah.viewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kessekolah.model.BookMarkViewModel
import com.example.kessekolah.model.DetailMateriViewModel
import com.example.kessekolah.model.ListMateriViewModel

class ViewModelFactoryBookMark(private val application: Application) : ViewModelProvider.NewInstanceFactory() {
    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactoryBookMark? = null

        @JvmStatic
        fun getInstance(application: Application): ViewModelFactoryBookMark {
            if (INSTANCE == null) {
                synchronized(ViewModelFactoryBookMark::class.java) {
                    INSTANCE = ViewModelFactoryBookMark(application)
                }
            }
            return INSTANCE as ViewModelFactoryBookMark
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookMarkViewModel::class.java)) {
            return BookMarkViewModel() as T
        } else if (modelClass.isAssignableFrom(DetailMateriViewModel::class.java)) {
            return DetailMateriViewModel(application) as T
        }
        else if (modelClass.isAssignableFrom(ListMateriViewModel::class.java)) {
            return ListMateriViewModel(application) as T
        }
        throw IllegalArgumentException("Cannot recognized ViewModel Class : " + modelClass.name)
    }
}