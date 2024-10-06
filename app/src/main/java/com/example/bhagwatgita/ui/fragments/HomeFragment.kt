package com.example.bhagwatgita.ui.fragments


import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.bhagwatgita.datasources.NetworkManager
import com.example.bhagwatgita.R
import com.example.bhagwatgita.ui.adapter.AdapterChapter
import com.example.bhagwatgita.databinding.FragmentHomeBinding
import com.example.bhagwatgita.datasources.model.ChaptersItem
import com.example.bhagwatgita.datasources.room.ChaptersInEnglish
import com.example.bhagwatgita.viewmodel.MainViewModel
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapterChapter: AdapterChapter
    private val viewModel: MainViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(layoutInflater)

        setStatusBarColor()

        observeNetworkConnectivity()

        binding.ivSettings.tooltipText = "Settings"

        goingToSettingsFragment()

        return binding.root
    }


    private fun observeNetworkConnectivity() {
        val networkManager = NetworkManager(requireContext())
        networkManager.observe(viewLifecycleOwner){hasInternet->
            if(hasInternet){
                binding.shimmer.visibility= View.VISIBLE
                binding.rvChapters.visibility = View.VISIBLE
                binding.tvShowingMessage.visibility = View.GONE
                getAllTheChapters()
            }
            else{
                binding.rvChapters.visibility = View.GONE
                binding.shimmer.visibility= View.GONE
                binding.tvShowingMessage.visibility = View.VISIBLE
            }
        }
    }
    private fun goingToSettingsFragment() {
        binding.ivSettings.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
        }
    }
    private fun getAllTheChapters() {
        lifecycleScope.launch {
            viewModel.getAllChapters().collect {
                adapterChapter = AdapterChapter(
                    requireContext(),
                    ::onChapterClick,
                    ::onFavoriteClick,
                    ::onChapterItemClickedSavedFragment,
                    true,
                    viewModel,
                    ::onFavoriteFilledClicked
                )
                binding.rvChapters.adapter = adapterChapter
                adapterChapter.differ.submitList(it)
                binding.shimmer.visibility = View.GONE
            }
        }
    }
    private fun onChapterClick(chaptersItem: ChaptersItem) {
        val bundle = Bundle()
        bundle.putInt("verseCount", chaptersItem.verses_count)
        bundle.putInt("chapterNumber", chaptersItem.chapter_number)
        bundle.putString("chapterTitle", chaptersItem.name_translated)
        bundle.putString("chapterDescription", chaptersItem.chapter_summary)

        findNavController().navigate(R.id.action_homeFragment_to_chapterFragment, bundle)
    }
    private fun onFavoriteClick(chaptersItem: ChaptersItem) {
        lifecycleScope.launch {
            viewModel.putChaptersItemId("Chapter ${chaptersItem.chapter_number}" , chaptersItem.id)
            viewModel.getVerseOfAChapter(chaptersItem.chapter_number).collect{verses->
                val versesListCh = arrayListOf<String>()
                for(verses in verses){   // 0 to 6
                    val versesList = verses.translations
                    for(currentVerse in versesList){
                        if(currentVerse.language == "english"){
                            versesListCh.add(currentVerse.description)
                            break
                        }
                    }
                }
                val englishChapters = ChaptersInEnglish(
                    chapter_number = chaptersItem.chapter_number,
                    chapter_summary = chaptersItem.chapter_summary,
                    chapter_summary_hindi = chaptersItem.chapter_summary_hindi,
                    id = chaptersItem.id,
                    name = chaptersItem.name,
                    name_meaning = chaptersItem.name_meaning,
                    name_translated = chaptersItem.name_translated,
                    name_transliterated = chaptersItem.name_transliterated,
                    slug = chaptersItem.slug,
                    verses_count = chaptersItem.verses_count,
                    verses = versesListCh
                )
                viewModel.insertEnglishChapter(englishChapters)
            }
        }
    }
   private  fun onFavoriteFilledClicked(chaptersItem: ChaptersItem){
       lifecycleScope.launch {
           viewModel.deleteOneEnglishChapter(chaptersItem.id)
           viewModel.deleteKeyFromSp("Chapter ${chaptersItem.chapter_number}")
       }

    }
    private fun onChapterItemClickedSavedFragment(chaptersItem: ChaptersItem){}
    private fun setStatusBarColor() {
        val window = activity?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        WindowCompat.getInsetsController(window!!, window.decorView).apply {
            isAppearanceLightStatusBars = true
        }
    }


}