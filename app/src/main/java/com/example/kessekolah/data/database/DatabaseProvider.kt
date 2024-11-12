package com.example.kessekolah.data.database

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    private var INSTANCE: MateriBookMarkRoomDatabase? = null

    fun getDatabase(context: Context): MateriBookMarkRoomDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                MateriBookMarkRoomDatabase::class.java,
                "materi_bookmark_database"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}
