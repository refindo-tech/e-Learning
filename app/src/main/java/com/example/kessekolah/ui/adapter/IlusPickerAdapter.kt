package com.example.kessekolah.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.kessekolah.databinding.ItemBannerMateriPickerBinding
import com.example.kessekolah.databinding.ItemButtonHomeFeaturesBinding
import com.example.kessekolah.model.ButtonCoreFeatures
import com.google.android.material.button.MaterialButton

class IlusPickerAdapter(private val list: List<Int>) :
    RecyclerView.Adapter<IlusPickerAdapter.ViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback
    private var selectedIlus: Int = 0

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    fun setSelectedIlus(number: Int) {
        selectedIlus = number
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemBannerMateriPickerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(number: Int) {
            binding.apply {
                val context = imgIlus.context

                imgIlus.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        context.getResources()
                            .getIdentifier("ilus_banner_$number", "drawable", context.getPackageName())
                    )
                )
                tvNumber.text = number.toString()
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ItemBannerMateriPickerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])

        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(list[holder.adapterPosition])
            // Update selected item
            setSelectedIlus(list[holder.adapterPosition])
        }
    }

    override fun getItemCount(): Int = list.size

    interface OnItemClickCallback {
        fun onItemClicked(data: Int)
    }
}