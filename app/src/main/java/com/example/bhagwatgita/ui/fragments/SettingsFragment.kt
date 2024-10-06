package com.example.bhagwatgita.ui.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.bhagwatgita.R
import com.example.bhagwatgita.databinding.FragmentSettingsBinding
import com.example.bhagwatgita.datasources.NetworkManager
import com.example.bhagwatgita.viewmodel.MainViewModel
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {
    private lateinit var binding : FragmentSettingsBinding
    private val viewModel: MainViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        binding = FragmentSettingsBinding.inflate(layoutInflater)
        setStatusBarColor()
        goingToChapterFragment()
        goingToSavedVerseFragment()
        getVerseOfTheDay()
//        settingTheme()
        return binding.root
    }
    private fun getVerseOfTheDay() {
        val chapterNumber = (1..18).random()
        val verseNumber = (1..20).random()
        val verse = viewModel.getVerseOfTheDay()
        if(verse.isNotEmpty()){
            for ((key, value) in verse) {
                val previousTime = key.toLong()
                val currentTime = System.currentTimeMillis()
                val timeDifference = currentTime - previousTime
                if (timeDifference > 24 * 60 * 60 * 1000){
                    getVerseOfTheDayFromAPI(chapterNumber, verseNumber)
                }
                else{
                    binding.tvVerseOfTheDay.text = value.toString()
                }
            }
        }
        else{
            getVerseOfTheDayFromAPI(chapterNumber,verseNumber)
        }
    }

    private fun getVerseOfTheDayFromAPI(chapterNumber : Int, verseNumber : Int){
        val networkManager = NetworkManager(requireContext())

        networkManager.observe(viewLifecycleOwner){
            if(it == true){
                lifecycleScope.launch {
                    viewModel.getParticularVerse(chapterNumber,verseNumber).collect{
                        val verseItem = it
                        for(verse in verseItem.translations){
                            if(verse.language == "english"){
                                viewModel.deleteVerseOfTheDay("")
                                viewModel.putVerseOfTheDay("${System.currentTimeMillis()}" , verse.description)
                                binding.tvVerseOfTheDay.text = verse.description
                                break
                            }
                        }
                    }
                }
            }
            else{
                val verse = viewModel.getVerseOfTheDay()
                for ((key, value) in verse) {
                    binding.tvVerseOfTheDay.text = value.toString()
                }
            }
        }
    }


    private fun goingToSavedVerseFragment() {
        binding.llSavedVerses.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_verseSavedFragment)
        }
    }

    private fun goingToChapterFragment() {
        binding.llSavedChapters.setOnClickListener {

            findNavController().navigate(R.id.action_settingsFragment_to_savedFragment)
        }
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