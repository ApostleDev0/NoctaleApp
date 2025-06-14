package com.example.noctaleapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.test.isSelected
import androidx.recyclerview.widget.RecyclerView
import com.example.noctaleapp.databinding.ItemGenreBinding
import com.example.noctaleapp.model.Genre

class GenreAdapter (
    private val genres: List<Genre>,
    private val onGenreClick: (Genre) -> Unit
    ):
    RecyclerView.Adapter<GenreAdapter.GenreViewHolder>() {

        private var selectedPosition: Int? = null
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

        override fun onBindViewHolder(holder: GenreViewHolder, @SuppressLint("RecyclerView") position: Int) {
            val genre = genres[position]

            holder.binding.genreName.text = genre.name
            val isSelected = selectedPosition == position
            holder.binding.genreName.isSelected = isSelected
            holder.binding.genreName.setOnClickListener {
//                val isSelected = !it.isSelected
//                it.isSelected = isSelected
                if (selectedPosition == position) {
                    selectedPosition = null
                } else {
                    val previousSelectedPosition = selectedPosition
                    selectedPosition = position
                    previousSelectedPosition?.let {notifyItemChanged(it)}
                }
                notifyItemChanged(position)
                onGenreClick(genre)
            }
        }
    }