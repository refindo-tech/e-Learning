package com.example.kessekolah.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kessekolah.R
import com.example.kessekolah.data.database.MateriData
import com.example.kessekolah.databinding.MateriItemSiswaListBinding

class MateriListSiswaAdapter :
    ListAdapter<MateriData, MateriListSiswaAdapter.ListViewHolder>(MateriListDiffCallback()) {
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    class ListViewHolder(var binding: MateriItemSiswaListBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            MateriItemSiswaListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (id, fileName, title, fileUrl, tahun, category, timeStamp, icon) = getItem(position)
        val data = getItem(position)

        with(holder.binding) {
            ivItemIcon.setImageDrawable(ContextCompat.getDrawable(holder.itemView.context, R.drawable.ic_book))
            tvItemTitle.text = data.judul
            tvItemCategory.text = data.category
            tvItemTime.text = data.timestamp

            holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(getItem(position))}
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: MateriData)
    }
}