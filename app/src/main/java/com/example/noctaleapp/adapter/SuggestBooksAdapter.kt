package com.example.noctaleapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.noctaleapp.R
import com.example.noctaleapp.databinding.ItemBookBinding
import com.example.noctaleapp.model.Book

class SuggestBooksAdapter(
    private var books: MutableList<Book> = mutableListOf(),
    private val onBookClick: (Book) -> Unit,
    private val onReadNowClick: (Book) -> Unit,
) : RecyclerView.Adapter<SuggestBooksAdapter.BookViewHolder>() {

    inner class BookViewHolder(val binding: ItemBookBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BookViewHolder {
        val binding = ItemBookBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: BookViewHolder,
        position: Int
    ) {
        val book = books[position]
        holder.binding.bookName.text = book.title
        holder.binding.bookAuthor.text = "Tác giả: ${book.author}"
        holder.binding.bookDescription.text = book.description
        holder.binding.chaptersBook.text = "Số chương: ${book.chapterQuantity}"
        holder.binding.ratingBook.rating = book.rating.toFloat()
        Glide.with(holder.itemView.context)
            .load(book.coverUrl)
            .placeholder(R.drawable.bookcover_template)
            .error(R.drawable.bookcover_template)
            .into(holder.binding.bookImage)
        holder.binding.readingButton.setOnClickListener{
            onReadNowClick(book)
        }
        holder.itemView.setOnClickListener {
            onBookClick(book)
        }
    }

    override fun getItemCount(): Int = books.size

    fun updateData(newBooks: List<Book>) {
        books.clear()
        books.addAll(newBooks)
        notifyDataSetChanged()
    }

}