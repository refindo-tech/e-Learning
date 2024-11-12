package com.example.kessekolah.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kessekolah.data.database.PostConsultation
import com.example.kessekolah.databinding.ItemListQuestionsBinding

class QuestionListAdapter(private val userRole: String?) : ListAdapter<PostConsultation, QuestionListAdapter.ListViewHolder>(DIFF_CALLBACK) {
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
            tvQuestions.text = data.question
            tvTime.text = data.timestamp

            if (userRole == "Guru") {
                btnDelete.setOnClickListener {
                    onItemClickCallback.onDeleteClicked(data)
                }
            } else {
                btnDelete.visibility = View.GONE
            }

            holder.itemView.setOnClickListener {
                onItemClickCallback.onItemClicked(data)
            }
        }
    }

    interface OnItemClickCallback {
        fun onDeleteClicked(data: PostConsultation)

        fun onItemClicked(data: PostConsultation)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PostConsultation>() {
            override fun areItemsTheSame(oldItem: PostConsultation, newItem: PostConsultation): Boolean {
                return oldItem.name == newItem.name
            }

            override fun areContentsTheSame(oldItem: PostConsultation, newItem: PostConsultation): Boolean {
                return oldItem == newItem
            }
        }
    }
}