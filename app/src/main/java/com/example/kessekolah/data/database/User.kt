package com.example.kessekolah.data.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "users")
@Parcelize
data class User(
    @PrimaryKey(autoGenerate = true)

    @ColumnInfo(name = "id")
    var id: Int,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "email")
    var email: String,

    @ColumnInfo(name = "userProfilePicture")
    var userProfilePicture: String,

    @ColumnInfo(name = "role")
    var role: String,

    @ColumnInfo(name = "uid")
    var uid: String,

    @ColumnInfo(name = "createdAt")
    var createdAt: String
) : Parcelable