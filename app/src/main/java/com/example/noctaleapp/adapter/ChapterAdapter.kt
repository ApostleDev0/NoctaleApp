package com.example.noctaleapp.adapter // Hoặc package adapter của bạn

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.noctaleapp.R
import com.example.noctaleapp.model.Chapter

// Định nghĩa một kiểu cho lambda xử lý sự kiện click
// (Chapter) -> Unit nghĩa là một hàm nhận một đối tượng Chapter và không trả về gì (Unit)
typealias OnChapterClickListener = (Chapter) -> Unit

class ChapterAdapter(private val onChapterClick: OnChapterClickListener) :
    ListAdapter<Chapter, ChapterAdapter.ChapterViewHolder>(ChapterDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chapters, parent, false) // Sử dụng layout item của bạn
        return ChapterViewHolder(view, onChapterClick)
    }

    override fun onBindViewHolder(holder: ChapterViewHolder, position: Int) {
        val chapter = getItem(position)
        holder.bind(chapter)
    }

    // ViewHolder để giữ tham chiếu đến các View của item và xử lý sự kiện click
    class ChapterViewHolder(
        itemView: View,
        private val onChapterClick: OnChapterClickListener
    ) : RecyclerView.ViewHolder(itemView) {

        // Giả sử root của item_chapter_horizontal.xml là TextView
        // hoặc nếu TextView có ID là textViewChapterTitleItem
        private val textViewChapterTitle: TextView = itemView.findViewById(R.id.textViewChapterTitleItem)
        // Nếu root của item_chapter_horizontal.xml chính là TextView thì có thể:
        // private val textViewChapterTitle: TextView = itemView as TextView

        private var currentChapter: Chapter? = null

        init {
            itemView.setOnClickListener {
                currentChapter?.let {
                    onChapterClick(it) // Gọi lambda khi item được click
                }
            }
        }

        fun bind(chapter: Chapter) {
            currentChapter = chapter
            // Giả sử bạn muốn hiển thị tiêu đề chính của chương
            // Hoặc có thể là "Chương X: Tiêu đề"
            textViewChapterTitle.text = chapter.mainTitle
            // Ví dụ: textViewChapterTitle.text = "Chương ${chapter.chapterNumber}: ${chapter.title}"
        }
    }

    // DiffUtil.ItemCallback để ListAdapter biết cách so sánh các item và cập nhật RecyclerView hiệu quả
    class ChapterDiffCallback : DiffUtil.ItemCallback<Chapter>() {
        override fun areItemsTheSame(oldItem: Chapter, newItem: Chapter): Boolean {
            // So sánh dựa trên ID duy nhất của Chapter
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Chapter, newItem: Chapter): Boolean {
            // So sánh nội dung của Chapter để xem có cần vẽ lại View không
            // Bạn có thể muốn so sánh các trường khác nếu chúng ảnh hưởng đến hiển thị
            return oldItem == newItem // Data class tự động implement equals() đúng cách
        }
    }
}