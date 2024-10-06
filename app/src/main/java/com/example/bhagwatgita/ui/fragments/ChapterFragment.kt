package com.example.bhagwatgita.ui.fragments

import android.os.Build
import android.os.Bundle
import android.speech.tts.UtteranceProgressListener
import android.text.StaticLayout
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.bhagwatgita.datasources.NetworkManager
import com.example.bhagwatgita.R
import com.example.bhagwatgita.TextToSpeechManager
import com.example.bhagwatgita.ui.adapter.AdapterVerses
import com.example.bhagwatgita.databinding.FragmentChapterBinding
import com.example.bhagwatgita.datasources.model.VerseModel
import com.example.bhagwatgita.viewmodel.MainViewModel
import kotlinx.coroutines.launch

class ChapterFragment : Fragment() {

    private lateinit var binding: FragmentChapterBinding
    private lateinit var adapterVerses: AdapterVerses
    //text to speech
    private lateinit var textToSpeechManager: TextToSpeechManager

    private val viewModel: MainViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChapterBinding.inflate(layoutInflater)

        //text to speech
        textToSpeechManager = TextToSpeechManager(requireActivity())
        onPlayClicked()
        onPausedClicked()
        setUtteranceProgressListener()

        setStatusBarColor()

        getAndSetChapterInfo()

//        val text = binding.tvChapterDescription.text.toString()
//        val truncatedText = getTruncatedText(text, 3)
//        binding.tvChapterDescription.text = truncatedText
        var expanded = false
        binding.tvSeeMore.setOnClickListener {
            if(!expanded){
                binding.tvChapterDescription.maxLines = 100
                binding.tvSeeMore.text = "See Less..."
                expanded = true
            }
            else{
                binding.tvChapterDescription.maxLines = 3
                binding.tvSeeMore.text = "Read More..."
                expanded = false
            }
        }
//        binding.tvSeeMore.setOnClickListener {
//            binding.tvChapterDescription.text = text // Expand to show full text
//        }
        observeNetworkConnectivity()

        lifecycleScope.launch { getDataFromRoom() }


        return binding.root
    }

    private fun setUtteranceProgressListener() {
        textToSpeechManager.setUtteranceProgressListener(object  : UtteranceProgressListener(){
            override fun onStart(p0: String?) {
                activity?.runOnUiThread{
                    binding.progress.visibility = View.GONE
                    binding.ivPause.visibility = View.VISIBLE
                }
            }
            override fun onDone(p0: String?) {
                activity?.runOnUiThread{
                    binding.ivPause.visibility = View.GONE
                    binding.ivPlay.visibility = View.VISIBLE
                    Toast.makeText(context , "Completed" , Toast.LENGTH_SHORT).show()
                }
            }

            override fun onError(p0: String?) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun onPausedClicked() {
        binding.ivPause.setOnClickListener {
            binding.ivPause.visibility = View.GONE
            binding.ivPlay.visibility = View.VISIBLE
            textToSpeechManager.stop(false)
        }
    }

    private fun onPlayClicked() {
        binding.ivPlay.setOnClickListener {
            binding.progress.visibility = View.VISIBLE
            binding.ivPlay.visibility = View.GONE
            textToSpeechManager.speak(binding.tvChapterDescription.text.toString())
        }
    }

    private fun getTruncatedText(text: String, maxLines: Int): String {
        val layout = StaticLayout.Builder.obtain(text, 0, text.length, binding.tvChapterDescription.paint, binding.tvChapterDescription.width).apply {
            setLineSpacing(binding.tvChapterDescription.lineSpacingExtra, binding.tvChapterDescription.lineSpacingMultiplier)
            setIncludePad(binding.tvChapterDescription.includeFontPadding)
            setMaxLines(maxLines)
        }.build()
        return text.subSequence(0, layout.getLineEnd(maxLines - 1)).toString()
    }

    private suspend fun getDataFromRoom() {
        val bundle = arguments
        val showDataFromRoom = bundle?.getBoolean("showRoomData", false)
        binding.shimmer.visibility = View.VISIBLE
        if (showDataFromRoom == true) {
            viewModel.getOneEnglishChapter(bundle.getInt("chapterNumber"))
                .observe(viewLifecycleOwner, Observer {
                    adapterVerses = AdapterVerses(::onVerseItemViewClicked, false, true)
                    binding.rvVerses.adapter = adapterVerses
                    val verseModelList = arrayListOf<VerseModel>()
                    for (verse in it.verses!!) {
                        verseModelList.add(VerseModel(it.chapter_number!!, verse, 0))
                    }
//                    val verseModel  = VerseModel(it.chapter_number!!,verseList,0)
                    adapterVerses.differ.submitList(verseModelList)
                    binding.rvVerses.visibility = View.VISIBLE
                    binding.shimmer.visibility = View.GONE
                    binding.tvShowingMessage.visibility = View.GONE
                })
        }

    }

    private fun observeNetworkConnectivity() {
        val bundle = arguments
        val showDataFromRoom = bundle?.getBoolean("showRoomData", false)

        if(showDataFromRoom == false){
            val networkManager = NetworkManager(requireContext())
            networkManager.observe(viewLifecycleOwner) { hasInternet ->
                if (hasInternet) {
                    binding.shimmer.visibility = View.VISIBLE
                    binding.rvVerses.visibility = View.VISIBLE
                    binding.tvShowingMessage.visibility = View.GONE
                    lifecycleScope.launch { getVersesOfTheChapter() }
                } else {
                    binding.rvVerses.visibility = View.GONE
                    binding.shimmer.visibility = View.GONE
                    binding.tvShowingMessage.visibility = View.VISIBLE
                }
            }
        }


    }

    private suspend fun getVersesOfTheChapter() {
        lifecycleScope.launch {
            val bundle = arguments
            val chapterNumber = bundle?.getInt("chapterNumber")!!
            Log.d("ch", chapterNumber.toString())
            viewModel.getVerseOfAChapter(chapterNumber).collect {
                val versesListCh = arrayListOf<String>()
                for (verses in it) {   // 0 to 6
                    val versesList = verses.translations
                    for (currentVerse in versesList) {
                        if (currentVerse.language == "english") {
                            versesListCh.add(currentVerse.description)
                            break
                        }
                    }
                }
                val verseModelList = arrayListOf<VerseModel>()
                for (verse in versesListCh) {
                    verseModelList.add(VerseModel(chapterNumber, verse, 0))
                }
                adapterVerses = AdapterVerses(::onVerseItemViewClicked, true, true)
                binding.rvVerses.adapter = adapterVerses
                adapterVerses.differ.submitList(verseModelList)
                binding.shimmer.visibility = View.GONE
            }
        }


    }

    private fun getAndSetChapterInfo() {
        val bundle = arguments
        binding.tvChapterNumber.text = "Chapter ${bundle?.getInt("chapterNumber")}"
        binding.tvChapterTitle.text = bundle?.getString("chapterTitle")
        binding.tvChapterDescription.text = bundle?.getString("chapterDescription")
        binding.tvNumberOfVerses.text = bundle?.getInt("verseCount").toString()


    }

    private fun onVerseItemViewClicked(verseNumber: Int, verseModel: VerseModel) {
        val bundle = Bundle()
        bundle.putInt("chapterNumber", binding.tvChapterNumber.text.substring(8).toInt())
        bundle.putInt("verseNumber", verseNumber)
        findNavController().navigate(R.id.action_chapterFragment_to_verseFragment, bundle)
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

    override fun onDestroy() {
        super.onDestroy()
        textToSpeechManager.stop(true)
    }
    override fun onPause() {
        super.onPause()
        textToSpeechManager.stop(true)
    }

}