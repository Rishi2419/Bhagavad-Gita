package com.example.bhagwatgita.ui.fragments

import android.os.Build
import android.os.Bundle
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.bhagwatgita.datasources.NetworkManager
import com.example.bhagwatgita.R
import com.example.bhagwatgita.TextToSpeechManager
import com.example.bhagwatgita.databinding.FragmentVerseBinding
import com.example.bhagwatgita.datasources.model.Commentary
import com.example.bhagwatgita.datasources.model.Translation
import com.example.bhagwatgita.datasources.model.VerseItem
import com.example.bhagwatgita.datasources.room.VersesInEnglish
import com.example.bhagwatgita.viewmodel.MainViewModel
import kotlinx.coroutines.launch


class VerseFragment() : Fragment() {
    private lateinit var binding: FragmentVerseBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var textToSpeechManager: TextToSpeechManager
    private var chapterNumber = 0
    private var verseNumber = 0
    private var verse = MutableLiveData<VerseItem>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVerseBinding.inflate(layoutInflater)
        setStatusBarColor()
        //text to speech
        textToSpeechManager = TextToSpeechManager(requireActivity())
        onPlayClicked()
        onPausedClicked()
        setUtteranceProgressListener()
        getChapterAndVerseNumber()
        observeNetworkConnectivity()
        getDataFromRoom()
        var expanded = false
        binding.tvSeeMore.setOnClickListener {
            if(!expanded){
                binding.tvTextCommentary.maxLines = 30
                expanded = true
            }
            else{
                binding.tvTextCommentary.maxLines = 4
                expanded = false
            }
        }
        savingFavoriteVerse()
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
            textToSpeechManager.speak(binding.tvWordIfEnglish.text.toString())
        }
    }

    private fun getDataFromRoom() {
        Log.d("VB" , "rd")
        val bundle = bundle()
        val showRoomData = bundle?.getBoolean("showRoomData", false)
        val chapterNum = bundle?.getInt("chapterNumber", 1)
        val verseNum = bundle?.getInt("verseNumber", 1)
        if (showRoomData == true) {
            binding.ivFavoriteVerse.visibility = View.GONE
            binding.ivFavoriteVerseFilled.visibility = View.GONE
            viewModel.getAParticularVerse(chapterNum!!, verseNum!!).observe(viewLifecycleOwner) {
                if (it != null) {
                    val verseDetail = it
                    binding.tvVerseNumber.text = "||$chapterNum.$verseNum||"
                    binding.tvVerseText.text = verseDetail.text
                    binding.tvTransliterationIfEnglish.text = verseDetail.transliteration
                    binding.tvWordIfEnglish.text = verseDetail.word_meanings


                    showTranslationList(it)

                    showCommentariesList(it)
                    handelVisibility()
                }

            }

        }
    }

    fun showTranslationList(it: VersesInEnglish) {
        val englishTranslationList = it.translations
        val engListSize = englishTranslationList.size
        if (englishTranslationList.isNotEmpty()) {
            binding.tvText.text = englishTranslationList[0].description
            binding.tvAuthorName.text = englishTranslationList[0].author_name
            if (engListSize == 1) binding.fabTranslationRight.visibility = View.GONE
        }
        var i = 0
        binding.fabTranslationRight.setOnClickListener {
            if (i < engListSize - 1) {
                i++
                binding.tvAuthorName.text = englishTranslationList[i].author_name
                binding.tvText.text = englishTranslationList[i].description
                binding.fabTranslationLeft.visibility = View.VISIBLE
                if (i == engListSize - 1) {
                    binding.fabTranslationRight.visibility = View.GONE
                }
            }
        }
        binding.fabTranslationLeft.setOnClickListener {
            if (i > 0) {
                i--
                binding.fabTranslationRight.visibility = View.VISIBLE
                binding.tvAuthorName.text = englishTranslationList[i].author_name
                binding.tvText.text = englishTranslationList[i].description
                if (i == 0) {
                    binding.fabTranslationLeft.visibility = View.GONE
                }
            }
        }
    }

    fun showCommentariesList(it: VersesInEnglish) {
        val englishCommentaryList = it.commentaries
        val engCommentaryListSize = englishCommentaryList.size
        if (englishCommentaryList.isNotEmpty()) {
            binding.tvTextCommentary.text = englishCommentaryList[0].description
            binding.tvAuthorNameCommentary.text = englishCommentaryList[0].author_name
            if (engCommentaryListSize == 1) binding.fabCommentaryRight.visibility =
                View.GONE
        }
        var j = 0
        binding.fabCommentaryRight.setOnClickListener {
            if (j < engCommentaryListSize - 1) {
                j++
                if (j == engCommentaryListSize - 1) {
                    binding.fabCommentaryRight.visibility = View.GONE
                }
                binding.fabCommentryLeft.visibility = View.VISIBLE
                binding.tvAuthorNameCommentary.text =
                    englishCommentaryList[j].author_name
                binding.tvTextCommentary.text = englishCommentaryList[j].description
            }
        }
        binding.fabCommentryLeft.setOnClickListener {
            if (j > 0) {
                j--
                if (j == 0) {
                    binding.fabCommentryLeft.visibility = View.GONE
                }
                binding.fabCommentaryRight.visibility = View.VISIBLE
                binding.tvAuthorNameCommentary.text =
                    englishCommentaryList[j].author_name
                binding.tvTextCommentary.text = englishCommentaryList[j].description
            }
        }
    }

    fun handelVisibility(){
        binding.progressbar.visibility = View.GONE
        binding.tvShowingMessage.visibility = View.GONE
        binding.tvVerseNumber.visibility = View.VISIBLE
        binding.tvVerseText.visibility = View.VISIBLE
        binding.tvTransliterationIfEnglish.visibility = View.VISIBLE
        binding.tvWordIfEnglish.visibility = View.VISIBLE
        binding.view.visibility = View.VISIBLE
        binding.tvTranslation.visibility = View.VISIBLE
        binding.clTranslation.visibility = View.VISIBLE
        binding.tvCommentries.visibility = View.VISIBLE
        binding.clCommentries.visibility = View.VISIBLE
        binding.backgroundImage.visibility = View.VISIBLE
    }

    private fun observeNetworkConnectivity() {
        val bundle = arguments
        val showRoomData = bundle?.getBoolean("showRoomData", false)
        if (showRoomData == false){
            val networkManager = NetworkManager(requireContext())
            networkManager.observe(viewLifecycleOwner) { hasInternet ->
                if (hasInternet) {
                    binding.progressbar.visibility = View.VISIBLE
                    binding.tvShowingMessage.visibility = View.GONE
                    getParticularVerse()
                } else {
                    binding.tvShowingMessage.visibility = View.VISIBLE
                    binding.progressbar.visibility = View.GONE
                    binding.tvVerseNumber.visibility = View.GONE
                    binding.tvVerseText.visibility = View.GONE
                    binding.tvTransliterationIfEnglish.visibility = View.GONE
                    binding.tvWordIfEnglish.visibility = View.GONE
                    binding.view.visibility = View.GONE
                    binding.tvTranslation.visibility = View.GONE
                    binding.clTranslation.visibility = View.GONE
                    binding.tvCommentries.visibility = View.GONE
                    binding.clCommentries.visibility = View.GONE
                    binding.backgroundImage.visibility = View.GONE

                }
            }
        }
    }

    private fun savingFavoriteVerse() {
        val verseKeys = viewModel.getAllSavedVersesNumber()
        val key = "$chapterNumber.$verseNumber"
        if(verseKeys.contains(key)){
            binding.ivFavoriteVerse.visibility = View.GONE
            val bundle = arguments
            val showRoomData = bundle?.getBoolean("showRoomData", false)
            if(showRoomData == false)   binding.ivFavoriteVerseFilled.visibility = View.VISIBLE

        }

        binding.ivFavoriteVerse.setOnClickListener {
            binding.ivFavoriteVerse.visibility = View.GONE
            binding.ivFavoriteVerseFilled.visibility = View.VISIBLE
            viewModel.putSavedVersesNumber(key,"verse")
            observeVerse()
        }

        binding.ivFavoriteVerseFilled.setOnClickListener {
            binding.ivFavoriteVerse.visibility = View.VISIBLE
            binding.ivFavoriteVerseFilled.visibility = View.GONE
            viewModel.deleteVerseNumber(key)
            lifecycleScope.launch { viewModel.deleteAParticularVerse(chapterNumber, verseNumber) }
        }

    }


    private fun getParticularVerse() {
        lifecycleScope.launch {
            viewModel.getParticularVerse(chapterNumber, verseNumber).collect { verseItem ->
                verse.value = verseItem
                binding.tvVerseText.text = verseItem.text
                binding.tvTransliterationIfEnglish.text = verseItem.transliteration
                binding.tvWordIfEnglish.text = verseItem.word_meanings

                // it is for english

                val englishTranslationList = arrayListOf<Translation>()
                for (translation in verseItem.translations) {
                    if (translation.language == "english"){
                        englishTranslationList.add(translation)
                    }
                }

                val engListSize = englishTranslationList.size
                if (englishTranslationList.isNotEmpty()) {
                    binding.tvText.text = englishTranslationList[0].description
                    binding.tvAuthorName.text = englishTranslationList[0].author_name
                    if (engListSize == 1) binding.fabTranslationRight.visibility = View.GONE
                }

                var i = 0

                binding.fabTranslationRight.setOnClickListener {
                    if (i < engListSize - 1) {
                        i++
                        binding.tvAuthorName.text = englishTranslationList[i].author_name
                        binding.tvText.text = englishTranslationList[i].description
                        binding.fabTranslationLeft.visibility = View.VISIBLE
                        if (i == engListSize - 1) {
                            binding.fabTranslationRight.visibility = View.GONE
                        }
                    }
                }
                binding.fabTranslationLeft.setOnClickListener {
                    if (i > 0) {
                        i--
                        binding.tvAuthorName.text = englishTranslationList[i].author_name
                        binding.tvText.text = englishTranslationList[i].description
                        binding.fabTranslationRight.visibility = View.VISIBLE
                        if (i == 0) {
                            binding.fabTranslationLeft.visibility = View.GONE
                        }
                    }
                }




                val englishCommentaryList = arrayListOf<Commentary>()
                val hindiCommentaryList = arrayListOf<Commentary>()
                for (commentary in verseItem.commentaries) {
                    if (commentary.language == "hindi") hindiCommentaryList.add(commentary)
                    if (commentary.language == "english") englishCommentaryList.add(commentary)
                }
                val engCommentaryListSize = englishCommentaryList.size
                if (englishCommentaryList.isNotEmpty()) {
                    binding.tvTextCommentary.text = englishCommentaryList[0].description
                    binding.tvAuthorNameCommentary.text = englishCommentaryList[0].author_name
                    if (engCommentaryListSize == 1) binding.fabCommentaryRight.visibility =
                        View.GONE
                }
                var j = 0
                binding.fabCommentaryRight.setOnClickListener {
                    if (j < engCommentaryListSize - 1) {
                        j++
                        if (j == engCommentaryListSize - 1) {
                            binding.fabCommentaryRight.visibility = View.GONE
                        }
                        binding.fabCommentryLeft.visibility = View.VISIBLE
                        binding.tvAuthorNameCommentary.text = englishCommentaryList[j].author_name
                        binding.tvTextCommentary.text = englishCommentaryList[j].description
                    }
                }
                binding.fabCommentryLeft.setOnClickListener {
                    if (j > 0) {
                        j--
                        if (j == 0) {
                            binding.fabCommentryLeft.visibility = View.GONE
                        }
                        binding.fabCommentaryRight.visibility = View.VISIBLE
                        binding.tvAuthorNameCommentary.text = englishCommentaryList[j].author_name
                        binding.tvTextCommentary.text = englishCommentaryList[j].description
                    }
                }

                binding.progressbar.visibility = View.GONE

                binding.tvVerseNumber.visibility = View.VISIBLE
                binding.tvVerseText.visibility = View.VISIBLE
                binding.tvTransliterationIfEnglish.visibility = View.VISIBLE
                binding.tvWordIfEnglish.visibility = View.VISIBLE
                binding.view.visibility = View.VISIBLE
                binding.tvTranslation.visibility = View.VISIBLE
                binding.clTranslation.visibility = View.VISIBLE
                binding.tvCommentries.visibility = View.VISIBLE
                binding.clCommentries.visibility = View.VISIBLE
                binding.backgroundImage.visibility = View.VISIBLE

            }
        }

    }

    private fun observeVerse() {
        verse.observe(viewLifecycleOwner, Observer {
            val englishTranslationList = arrayListOf<Translation>()
            for (translation in it.translations) {
                if (translation.language == "english") englishTranslationList.add(translation)
            }
            val englishCommentaryList = arrayListOf<Commentary>()
            for (commentary in it.commentaries) {
                if (commentary.language == "english") englishCommentaryList.add(commentary)
            }
            val verseInEnglish = VersesInEnglish(
                it.chapter_number,
                englishCommentaryList,
                it.id,
                it.slug,
                it.text,
                englishTranslationList,
                it.transliteration,
                it.verse_number,
                it.word_meanings
            )
            lifecycleScope.launch { viewModel.insertEnglishVerse(verseInEnglish) }
        })
    }

    // can set it from api data not need to pass ,verseItem has it already
    private fun getChapterAndVerseNumber() {
        val bundle = arguments
        chapterNumber = bundle!!.getInt("chapterNumber")
        verseNumber = bundle.getInt("verseNumber")
        binding.tvVerseNumber.text = "||$chapterNumber.$verseNumber||"
    }

    private fun bundle() = arguments


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
        textToSpeechManager.stop(true) // Stop the speech when the fragment goes into the background
    }

}