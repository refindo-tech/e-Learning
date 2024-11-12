package com.example.kessekolah.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kessekolah.data.database.MateriData
import com.example.kessekolah.databinding.BannerDetailMateriBinding

class BannerDetailMateriAdapter:
    ListAdapter<MateriData, BannerDetailMateriAdapter.ListViewHolder>(MateriListDiffCallback()) {

    class ListViewHolder(var binding: BannerDetailMateriBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            BannerDetailMateriBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (id, title, fileUrl, category, timeStamp, icon) = getItem(position)
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

        }
    }


    interface OnItemClickCallback {
        fun onItemClicked(data: MateriData)
    }
}