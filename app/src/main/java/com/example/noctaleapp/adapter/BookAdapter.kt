package com.example.noctaleapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewParent
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.noctaleapp.R
import com.example.noctaleapp.databinding.ItemBookBinding
import com.example.noctaleapp.model.Book

class BookAdapter(private val books: List<Book>):
    RecyclerView.Adapter<BookAdapter.BookViewHolder>() {
    inner class BookViewHolder(val binding: ItemBookBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemBookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookViewHolder(binding)
    }

    override fun getItemCount() = books.size

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]
        val context = holder.itemView.context

        holder.binding.bookName.text = book.title
        holder.binding.bookAuthor.text = "Tác giả: ${book.author}"
        holder.binding.ratingBook.rating = book.rating
        holder.binding.chaptersBook.text = "Số chương: ${book.numberChapter}"
        holder.binding.bookDescription.text = book.description
        if (book.imageUrl != null) {
            Glide.with(context)
                .load(book.imageUrl)
                .placeholder(R.drawable.bookcover_template)
                .into(holder.binding.bookImage)
        } else if (book.localImageRes != null) {
            Glide.with(context)
                .load(book.localImageRes)
                .into(holder.binding.bookImage)
        }
    }
}