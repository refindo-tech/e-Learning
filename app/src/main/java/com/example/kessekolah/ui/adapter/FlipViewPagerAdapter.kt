package com.example.kessekolah.ui.adapter

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.example.kessekolah.R

class ScreenSlideRecyclerAdapter(val itemsList: ArrayList<Bitmap>) : RecyclerView.Adapter<ScreenSlideRecyclerAdapter.ScreenSlideViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScreenSlideViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_dummy_layout, parent, false)
        return ScreenSlideViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScreenSlideViewHolder, position: Int) {
        holder.bind(itemsList[position], position)
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    fun setItems(items: List<Bitmap>)
    {
        itemsList.clear()
        itemsList.addAll(items)
        notifyDataSetChanged()
    }

    inner class ScreenSlideViewHolder : RecyclerView.ViewHolder
    {
        var imageView: ImageView? = null
        constructor(view: View) : super(view) {
            imageView = view.findViewById(R.id.imageView)
        }

        fun bind(img: Bitmap, position: Int) {
            imageView?.setImageBitmap(img)
//            imageView?.setText(position.toString())
        }
    }
}