package com.example.kessekolah.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kessekolah.R
import com.example.kessekolah.data.database.MateriData
import com.example.kessekolah.databinding.BookmarkItemListBinding

class BookMarkAdapter :
    ListAdapter<MateriData, BookMarkAdapter.ListViewHolder>(MateriListDiffCallback())  {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    class ListViewHolder(var binding: BookmarkItemListBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            BookmarkItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    fun filterList(filterlist: ArrayList<MateriData>) {
        this.submitList(filterlist)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = getItem(position)

        with(holder.binding) {
            ivItemIcon.setImageDrawable(ContextCompat.getDrawable(holder.itemView.context, R.drawable.ic_book))
            tvItemTitle.text = data.judul
            tvItemCategory.text = data.category
            tvItemTime.text = data.timestamp
            btnDelete.setOnClickListener {
                onItemClickCallback.onDeleteClicked(data)
            }

            holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(data) }
        }
    }

    interface OnItemClickCallback {
        fun onDeleteClicked(data: MateriData)

        fun onItemClicked(data: MateriData)
    }
}



