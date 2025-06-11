package com.example.noctaleapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.noctaleapp.databinding.ItemGenreBinding
import com.example.noctaleapp.model.Genre

class GenreAdapter (private val genres: List<Genre>):
    RecyclerView.Adapter<GenreAdapter.GenreViewHolder>() {
        inner class GenreViewHolder(val binding: ItemGenreBinding): RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
            val binding = ItemGenreBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return GenreViewHolder(binding)
        }

        override fun getItemCount(): Int = genres.size

        override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
            val genre = genres[position]

            holder.binding.genreName.text = genre.name
        }
    }