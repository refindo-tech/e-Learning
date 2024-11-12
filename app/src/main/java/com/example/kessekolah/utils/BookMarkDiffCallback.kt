package com.example.kessekolah.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.kessekolah.data.database.MateriData

class BookMarkDiffCallback(private val mOldBookMarkList: List<MateriData>,
                           private val mNewBookMarkList: List<MateriData>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return mOldBookMarkList.size
    }

    override fun getNewListSize(): Int {
        return mNewBookMarkList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return mOldBookMarkList[oldItemPosition].judul == mNewBookMarkList[newItemPosition].judul
    }
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldUser = mOldBookMarkList[oldItemPosition]
        val newUser = mNewBookMarkList[newItemPosition]

        return oldUser.judul == newUser.judul && oldUser.category == newUser.category
    }
}