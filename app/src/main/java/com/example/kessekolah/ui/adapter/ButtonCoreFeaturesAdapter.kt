package com.example.kessekolah.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.kessekolah.databinding.ItemButtonHomeFeaturesBinding
import com.example.kessekolah.model.ButtonCoreFeatures

class ButtonCoreFeaturesAdapter(private val list: List<ButtonCoreFeatures>) :
    RecyclerView.Adapter<ButtonCoreFeaturesAdapter.ViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class ViewHolder(val view: ItemButtonHomeFeaturesBinding) :
        RecyclerView.ViewHolder(view.root) {
        private val binding = view
        fun bind(data: ButtonCoreFeatures) {
            binding.apply {
                val nameIcon = data.icon
                val context = btnCore.context
                tvButtonCore.text = data.title
//                (btnSrcCore as MaterialButton).icon = ContextCompat.getDrawable(
//                    context,
//                    context.getResources()
//                        .getIdentifier(data.icon, "drawable", context.getPackageName())
//                );

                btnSrcCore.setImageDrawable(ContextCompat.getDrawable(
                    context,
                    context.getResources()
                        .getIdentifier(data.icon, "drawable", context.getPackageName())
                ))

                btnCore.setOnClickListener { onItemClickCallback.onItemClicked(data.title) }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ItemButtonHomeFeaturesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])

        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(list[holder.adapterPosition].title) }
    }

    override fun getItemCount(): Int = list.size

    interface OnItemClickCallback {
        fun onItemClicked(data: String)
    }

}