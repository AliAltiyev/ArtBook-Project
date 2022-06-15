package com.example.artbookkotlin

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.artbookkotlin.databinding.RecyclerRawBinding


class ArtAdapter(val context: Context, private val list: List<Art>) :
    RecyclerView.Adapter<ArtAdapter.ViewHolder>() {

    class ViewHolder(val binding: RecyclerRawBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerRawBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.recyclerRawTextView.text = list[position].artName


    }

    override fun getItemCount() = list.size
}