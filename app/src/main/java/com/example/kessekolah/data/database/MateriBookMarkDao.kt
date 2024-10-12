package com.example.kessekolah.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MateriBookMarkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(materiBookMark: MateriData)

    @Query("DELETE FROM MateriData WHERE MateriData.id = :id")
    fun delete(id: Int)

    @Query("SELECT * FROM MateriData ORDER BY judul ASC")
    fun getAllFavorite(): LiveData<List<MateriData>>

    @Query("SELECT * FROM MateriData WHERE MateriData.id = :id")
    fun getDataByUsername(id: Int): LiveData<MateriData>
}