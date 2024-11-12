package com.example.kessekolah.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kessekolah.data.database.VideoList
import com.example.kessekolah.databinding.ItemBannerMateriBinding

class VideoListAdapterCore : ListAdapter<VideoList, VideoListAdapterCore.ListViewHolder>(DIFF_CALLBACK) {
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    class ListViewHolder(var binding: ItemBannerMateriBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemBannerMateriBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = getItem(position)

        with(holder.binding) {
            ilusBanner.setImageDrawable(
                ContextCompat.getDrawable(
                    holder.itemView.context,
                    holder.itemView.context.getResources()
                        .getIdentifier("ilus_banner_${data.dataIlus}", "drawable", holder.itemView.context.getPackageName())
                )
            )
            backColorBanner.setBackgroundColor(
                Color.parseColor(data.backColorBanner)
            )
            textNews.text = data.judul

            holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(getItem(position))}

        }
    }

    interface OnItemClickCallback {
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
