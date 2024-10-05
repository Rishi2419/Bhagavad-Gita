package com.example.bhagwatgita.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.bhagwatgita.databinding.ItemViewVersesBinding
import com.example.bhagwatgita.datasources.model.VerseModel
import kotlin.reflect.KFunction2

class AdapterVerses(
    val onVerseItemViewClicked: KFunction2<Int, VerseModel, Unit>,
    val showNextButton: Boolean,
    val showVerseNumber: Boolean
) : RecyclerView.Adapter<AdapterVerses.VersesViewHolder>() {

    class VersesViewHolder(val binding: ItemViewVersesBinding) : ViewHolder(binding.root)

    val diffUtil = object : DiffUtil.ItemCallback<VerseModel>() {
        override fun areItemsTheSame(oldItem: VerseModel, newItem: VerseModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: VerseModel, newItem: VerseModel): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VersesViewHolder {
        return VersesViewHolder(
            ItemViewVersesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: VersesViewHolder, position: Int) {
        val verse = differ.currentList[position]

        if (showNextButton) {
            holder.binding.ivNext.visibility = View.VISIBLE
        } else holder.binding.ivNext.visibility = View.GONE


        holder.binding.apply {

            if(showVerseNumber){
                tvVerseNumber.text = "Verse ${position + 1}"
            }
            else{
                tvVerseNumber.text = "Verse ${verse.chapterNumber}.${verse.verseNumber}"
            }
            tvVerse.text = verse.verse
//            val verses = verse.translations
//            for(i in verses){   // 0 to 6
//                val firstVerse = i.language
//                if(firstVerse == "english"){
//                    tvVerse.text = i.description
//                    break
//                }
//            }
        }

        holder.binding.ll.setOnClickListener {
            if(showNextButton){
                onVerseItemViewClicked(position + 1 , verse)
            }
        }
    }
}