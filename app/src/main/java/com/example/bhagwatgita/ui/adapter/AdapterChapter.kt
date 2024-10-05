package com.example.bhagwatgita.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.bhagwatgita.databinding.ItemViewChaptersBinding
import com.example.bhagwatgita.datasources.model.ChaptersItem
import com.example.bhagwatgita.viewmodel.MainViewModel
import kotlin.reflect.KFunction1

class AdapterChapter(
    val context: Context,
    val onChapterClick: (ChaptersItem) -> Unit,
    private val onFavoriteClick: ((ChaptersItem) -> Unit)?,
    val onChapterItemClickedSavedFragment: (ChaptersItem) -> Unit,
    private val showFavButton: Boolean,
    private val viewModel: MainViewModel,
    val onFavoriteFilledClicked: KFunction1<ChaptersItem, Unit>
) : RecyclerView.Adapter<AdapterChapter.ChapterViewHolder>() {
    class ChapterViewHolder(val binding : ItemViewChaptersBinding) : ViewHolder(binding.root)


    private val diffUtil = object  : DiffUtil.ItemCallback<ChaptersItem>(){
        override fun areItemsTheSame(oldItem: ChaptersItem, newItem: ChaptersItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ChaptersItem, newItem: ChaptersItem): Boolean {
            return oldItem == newItem
        }
    }

    val  differ = AsyncListDiffer(this,diffUtil)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterViewHolder {
        return ChapterViewHolder(ItemViewChaptersBinding.inflate(LayoutInflater.from(parent.context) , parent, false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ChapterViewHolder, position: Int) {
        val chapter = differ.currentList[position]


        val keys = viewModel.getAllKeys()
        if(showFavButton){
            if(keys.contains("Chapter ${chapter.chapter_number}")){
                Log.d("chapter" , chapter.chapter_number.toString())
                holder.binding.ivFavorite.visibility = View.GONE
                holder.binding.ivFavoriteFilled.visibility = View.VISIBLE
            }
            else{
                holder.binding.ivFavorite.visibility = View.VISIBLE
                holder.binding.ivFavoriteFilled.visibility = View.GONE
            }
        }
        else{
            holder.binding.ivFavorite.visibility = View.GONE
            holder.binding.ivFavoriteFilled.visibility = View.GONE
        }


        holder.binding.apply {
            tvChapterNumber.text = "Chapter " +chapter.chapter_number.toString()
            tvChapterTitle.text = chapter.name_translated.toString()
            tvChapterDescription.text = chapter.chapter_summary
            tvVersesCount.text = chapter.verses_count.toString()
        }

        holder.binding.ll.setOnClickListener {
            onChapterClick(chapter)
            onChapterItemClickedSavedFragment(chapter)
        }

        holder.binding.ivFavorite.setOnClickListener {
            holder.binding.apply {
                ivFavorite.visibility = View.GONE
                ivFavoriteFilled.visibility = View.VISIBLE
            }
            onFavoriteClick?.let { it1 -> it1(chapter) }

        }

        holder.binding.ivFavoriteFilled.setOnClickListener {
            holder.binding.apply {
                ivFavorite.visibility = View.VISIBLE
                ivFavoriteFilled.visibility = View.GONE
            }
            onFavoriteFilledClicked(chapter)
        }



    }
}