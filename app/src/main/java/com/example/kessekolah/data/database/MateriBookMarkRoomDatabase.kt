package com.example.kessekolah.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [MateriData::class], version = 1)
abstract class MateriBookMarkRoomDatabase : RoomDatabase() {
    abstract fun materiBookMarkDao(): MateriBookMarkDao

    companion object {
        @Volatile
        private var INSTANCE: MateriBookMarkRoomDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): MateriBookMarkRoomDatabase {
            if (INSTANCE == null) {
                synchronized(MateriBookMarkRoomDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        MateriBookMarkRoomDatabase::class.java, "bookmark_materi_database")
                        .build()
                }
            }
            return INSTANCE as MateriBookMarkRoomDatabase
        }
    }
}