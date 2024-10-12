package com.example.kessekolah.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kessekolah.R
import com.example.kessekolah.data.database.Answer
import com.example.kessekolah.databinding.ItemListQuestionsBinding
import com.squareup.picasso.Picasso

class AnswerListAdapter (private val userRole: String?) : ListAdapter<Answer, AnswerListAdapter.ListViewHolder>(DIFF_CALLBACK) {
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    class ListViewHolder(var binding: ItemListQuestionsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemListQuestionsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = getItem(position)

        with(holder.binding) {
            tvName.text = data.name
            tvQuestions.text = data.answerText
            tvTime.text = data.timestamp

            if (userRole == "Guru") {
                btnDelete.setOnClickListener {
                    onItemClickCallback.onDeleteClicked(data)
                }
            } else {
                btnDelete.visibility = View.GONE
            }
            circleImageView.setImageDrawable(ContextCompat.getDrawable(holder.itemView.context, R.drawable.ic_professional))

        }
    }

    interface OnItemClickCallback {
        fun onDeleteClicked(data: Answer)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Answer>() {
            override fun areItemsTheSame(oldItem: Answer, newItem: Answer): Boolean {
                return oldItem.name == newItem.name
            }

            override fun areContentsTheSame(oldItem: Answer, newItem: Answer): Boolean {
                return oldItem == newItem
            }
        }
    }
}