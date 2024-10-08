package com.example.retrofitlearn.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.retrofitlearn.R
import com.example.retrofitlearn.databinding.RowLayoutBinding
import com.example.retrofitlearn.model.PostModel

class RecyclerViewAdapter(private val postList: ArrayList<PostModel>) :
    RecyclerView.Adapter<RecyclerViewAdapter.RowHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowHolder {
        val view = RowLayoutBinding.inflate(LayoutInflater.from(parent.context))
        return RowHolder(view)
    }

    override fun getItemCount(): Int {
        return postList.count()
    }

    override fun onBindViewHolder(holder: RowHolder, position: Int) {
        holder.bind(postList[position])
    }

    class RowHolder(private val binding: RowLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(postModel: PostModel) {
            binding.title.text = postModel.title
            binding.body.text = postModel.body
        }
    }
}