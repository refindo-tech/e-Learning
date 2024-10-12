package com.example.kessekolah.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    fun insertUser(email: String, password: String): LiveData<User>
//
//    @Update
//    fun update(user: User)
//
//
//    @Query("SELECT * FROM users WHERE email = :email")
//    fun getUserByEmail(email: String): User?

}