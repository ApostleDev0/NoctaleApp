package com.example.noctaleapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.noctaleapp.R
import com.example.noctaleapp.databinding.ItemBookLibraryBinding
import com.example.noctaleapp.model.LibraryBook

class LibraryBookAdapter (
    private val books: MutableList<LibraryBook>,
    private val onLongClick: (LibraryBook) -> Unit,
    private val onSaveClick: (LibraryBook) -> Unit,
    private val onItemClick: (LibraryBook) -> Unit
) : RecyclerView.Adapter<LibraryBookAdapter.LBBookViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LBBookViewHolder {
        val binding = ItemBookLibraryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LBBookViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: LBBookViewHolder,
        position: Int
    ) {
        val book = books[position]
        holder.binding.bookName.text = book.title
        holder.binding.bookAuthor.text = book.author
        holder.binding.progressRead.progress = book.process
        Glide.with(holder.itemView.context)
            .load(book.coverUrl)
            .placeholder(R.drawable.bookcover_template)
            .error(R.drawable.bookcover_template)
            .into(holder.binding.bookImage)
        holder.binding.root.setOnLongClickListener {
            onLongClick(book)
            true
        }
        holder.binding.saveButton.setOnClickListener {
            onSaveClick(book)
        }
        holder.binding.root.setOnClickListener {
            onItemClick(book)

        }

    }

    fun updateData(newBooks: List<LibraryBook>) {
        (books as MutableList).clear()
        (books as MutableList).addAll(newBooks)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = books.size

    inner class LBBookViewHolder(val binding: ItemBookLibraryBinding) : RecyclerView.ViewHolder(binding.root)


}