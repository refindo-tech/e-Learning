package com.example.kessekolah.data.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class MateriData(
    @field:ColumnInfo(name = "id")
    @field:PrimaryKey
    var id: Int,

    @ColumnInfo
    var uid: String,

    @ColumnInfo
    var judul: String,

    @ColumnInfo
    var tahun: String,

    @ColumnInfo
    var category: String,

    @ColumnInfo
    var fileName: String,

    @ColumnInfo
    var fileUrl: String,

    @ColumnInfo
    var timestamp: String,

    @ColumnInfo
    var dataIlus: Int,

    @ColumnInfo
    var backColorBanner: String,
) : Parcelable
