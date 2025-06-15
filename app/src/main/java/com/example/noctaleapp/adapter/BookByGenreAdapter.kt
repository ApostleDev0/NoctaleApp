package com.example.noctaleapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.noctaleapp.R
import com.example.noctaleapp.databinding.ItemBookCardBinding
import com.example.noctaleapp.model.Book

class BookByGenreAdapter (
    private val books: List<Book>,
    private val onBookClick: (Book) -> Unit
    ) :
    RecyclerView.Adapter<BookByGenreAdapter.BookViewHolder>() {

    inner class BookViewHolder(val binding: ItemBookCardBinding): RecyclerView.ViewHolder(binding.root)

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemBookCardBinding.inflate(
            LayoutInflater.from(parent.context),
                parent,
                false
        )
            return BookViewHolder(binding)
    }

    override fun getItemCount(): Int = books.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]
        holder.binding.bookTitle.text = book.title
        holder.binding.bookAuthor.text = "Tác giả: ${book.author}"
        holder.binding.bookRating.rating = book.rating.toFloat()
        Glide.with(holder.itemView.context)
            .load(book.coverUrl)
            .placeholder(R.drawable.bookcover_template)
            .error(R.drawable.bookcover_template)
            .into(holder.binding.bookImage)
    }
}