package com.example.bhagwatgita.ui.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.bhagwatgita.R
import com.example.bhagwatgita.ui.adapter.AdapterVerses
import com.example.bhagwatgita.databinding.FragmentVerseSavedBinding
import com.example.bhagwatgita.datasources.model.VerseModel
import com.example.bhagwatgita.viewmodel.MainViewModel
import kotlinx.coroutines.launch


class SavedVerseFragment : Fragment() {
    private lateinit var binding : FragmentVerseSavedBinding
    private lateinit var adapterVerses: AdapterVerses
    private val viewModel: MainViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVerseSavedBinding.inflate(layoutInflater)
        setStatusBarColor()

        getAllEnglishVerses()


        return binding.root
    }

    private fun getAllEnglishVerses() {
        lifecycleScope.launch {
            viewModel.getAllEnglishVerse().observe(viewLifecycleOwner){
                val versesList = arrayListOf<String>()
                for(verse in it){
                    versesList.add(verse.translations[0].description)
                }
                val verseModelList = arrayListOf<VerseModel>()
                for(verse in it.indices){
                    verseModelList.add(VerseModel(it[verse].chapter_number,it[verse].translations[0].description,it[verse].verse_number))
                }

                if(verseModelList.isEmpty()){
                    binding.tvShowingMessage.visibility = View.VISIBLE
                }
                adapterVerses = AdapterVerses(::onVerseItemViewClicked , true,false)
                binding.rvVerses.adapter = adapterVerses
                adapterVerses.differ.submitList(verseModelList)
                binding.shimmer.visibility = View.GONE
            }
        }
    }

    private fun onVerseItemViewClicked(verseNumber: Int , verseModel : VerseModel){
        val bundle = Bundle()
        bundle.putBoolean("showRoomData" , true)
        bundle.putInt("chapterNumber" , verseModel.chapterNumber)
        bundle.putInt("verseNumber" , verseModel.verseNumber)
        findNavController().navigate(R.id.action_verseSavedFragment_to_verseFragment, bundle)
    }

    private fun setStatusBarColor() {
        activity?.window?.apply {
            val statusBarColors = ContextCompat.getColor(requireContext(), R.color.white)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }


}