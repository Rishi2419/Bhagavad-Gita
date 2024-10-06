package com.example.bhagwatgita.ui.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.bhagwatgita.R
import com.example.bhagwatgita.ui.adapter.AdapterChapter
import com.example.bhagwatgita.databinding.FragmentSavedBinding
import com.example.bhagwatgita.datasources.model.ChaptersItem
import com.example.bhagwatgita.viewmodel.MainViewModel

class SavedChapterFragment : Fragment() {

    private lateinit var binding: FragmentSavedBinding
    private lateinit var adapterChapter: AdapterChapter
    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSavedBinding.inflate(layoutInflater)

        setStatusBarColor()
        getTheSavedChapters()

        getAParticular()
        return binding.root
    }

    private fun getAParticular() {
        viewModel.getTheSavedChapters().observe(viewLifecycleOwner){chapterInEnglishLIst->

            for(chapter in chapterInEnglishLIst){
                val  n = chapter.chapter_number
                val verseCount = chapter.verses_count
            }

        }
    }


    private fun onChapterItemClickedSavedFragment(chaptersItem: ChaptersItem){
        val bundle = Bundle()
        bundle.putInt("verseCount", chaptersItem.verses_count)
        bundle.putInt("chapterNumber", chaptersItem.chapter_number)
        bundle.putString("chapterTitle", chaptersItem.name_translated)
        bundle.putString("chapterDescription", chaptersItem.chapter_summary)
        bundle.putBoolean("showRoomData" , true)
        findNavController().navigate(R.id.action_savedFragment_to_chapterFragment , bundle)
    }


    private fun getTheSavedChapters() {
        viewModel.getTheSavedChapters().observe(viewLifecycleOwner, Observer {
            val chaptersList = ArrayList<ChaptersItem>()
            for (chapters in it) {
                val chapterItem = ChaptersItem(
                    chapters.chapter_number!!,
                    chapters.chapter_summary!!,
                    chapters.chapter_summary_hindi!!,
                    chapters.id!!,
                    chapters.name!!,
                    chapters.name_meaning!!,
                    chapters.name_translated!!,
                    chapters.name_transliterated!!,
                    chapters.slug!!,
                    chapters.verses_count!!
                )
                chaptersList.add(chapterItem)
            }

            if(chaptersList.isEmpty()){
                binding.shimmer.visibility = View.GONE
                binding.rvChapters.visibility = View.GONE
                binding.tvShowingMessage.visibility = View.GONE
            }

            adapterChapter = AdapterChapter(requireActivity(),
                ::onChapterClicked,
                null,
                ::onChapterItemClickedSavedFragment,
                false,
                viewModel,
                ::onFavoriteFilledClicked
            )
            binding.rvChapters.adapter = adapterChapter
            adapterChapter.differ.submitList(chaptersList)
            binding.shimmer.visibility = View.GONE
        })
    }


    private fun onFavoriteFilledClicked(chaptersItem: ChaptersItem){}
    private fun setStatusBarColor() {
        activity?.window?.apply {
            val statusBarColors = ContextCompat.getColor(requireContext(), R.color.white)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }
    private fun onChapterClicked(chaptersItem: ChaptersItem){

    }




}