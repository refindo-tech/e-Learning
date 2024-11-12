package com.example.kessekolah.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.kessekolah.data.database.MateriData

class MateriListDiffCallback : DiffUtil.ItemCallback<MateriData>() {
    override fun areItemsTheSame(oldItem: MateriData, newItem: MateriData): Boolean {
        return oldItem.judul == newItem.judul
    }

    override fun areContentsTheSame(oldItem: MateriData, newItem: MateriData): Boolean {
        return oldItem == newItem
    }
}