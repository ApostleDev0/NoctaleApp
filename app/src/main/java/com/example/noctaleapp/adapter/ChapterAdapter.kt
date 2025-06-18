package com.example.noctaleapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.noctaleapp.R
import com.example.noctaleapp.model.Chapter

typealias OnChapterClickListener = (Chapter) -> Unit

class ChapterAdapter(private val onChapterClick: OnChapterClickListener) :
    ListAdapter<Chapter, ChapterAdapter.ChapterViewHolder>(ChapterDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chapters, parent, false)
        return ChapterViewHolder(view, onChapterClick)
    }

    override fun onBindViewHolder(holder: ChapterViewHolder, position: Int) {
        val chapter = getItem(position)
        holder.bind(chapter)
    }

    class ChapterViewHolder(
        itemView: View,
        private val onChapterClick: OnChapterClickListener
    ) : RecyclerView.ViewHolder(itemView) {
        private val textViewChapterTitle: TextView = itemView.findViewById(R.id.textViewChapterTitleItem)
        private var currentChapter: Chapter? = null

        init {
            itemView.setOnClickListener {
                currentChapter?.let {
                    onChapterClick(it)
                }
            }
        }

        fun bind(chapter: Chapter) {
            currentChapter = chapter
            textViewChapterTitle.text = "   ${chapter.mainTitle}"
        }
    }

    class ChapterDiffCallback : DiffUtil.ItemCallback<Chapter>() {
        override fun areItemsTheSame(oldItem: Chapter, newItem: Chapter): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Chapter, newItem: Chapter): Boolean {
            return oldItem == newItem
        }
    }
}