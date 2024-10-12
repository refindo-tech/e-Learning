package com.example.kessekolah.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kessekolah.R
import com.example.kessekolah.data.database.MateriData
import com.example.kessekolah.data.database.VideoList
import com.example.kessekolah.databinding.ItemVideoListBinding

class VideoListAdapter: ListAdapter<VideoList, VideoListAdapter.ListViewHolder>(DIFF_CALLBACK) {
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    class ListViewHolder(var binding: ItemVideoListBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemVideoListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    fun filterList(filterlist: ArrayList<VideoList>) {
        this.submitList(filterlist)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = getItem(position)

        with(holder.binding) {
            ivItemIcon.setImageDrawable(ContextCompat.getDrawable(holder.itemView.context, R.drawable.ic_baseline_smart_display_24))
            tvItemTitle.text = data.judul
            tvItemCategory.text = data.category
            tvItemTime.text = data.timestamp
            btnDelete.setOnClickListener {
                onItemClickCallback.onDeleteClicked(data)
            }
            btnEdit.setOnClickListener {
                onItemClickCallback.onEditClicked(data)
            }

            holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(getItem(position))}
        }
    }

    interface OnItemClickCallback {
        fun onDeleteClicked(data: VideoList)
        fun onEditClicked(data: VideoList)

        fun onItemClicked(data: VideoList)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<VideoList>() {
            override fun areItemsTheSame(oldItem: VideoList, newItem: VideoList): Boolean {
                return oldItem.judul == newItem.judul
            }

            override fun areContentsTheSame(oldItem: VideoList, newItem: VideoList): Boolean {
                return oldItem == newItem
            }
        }
    }
}