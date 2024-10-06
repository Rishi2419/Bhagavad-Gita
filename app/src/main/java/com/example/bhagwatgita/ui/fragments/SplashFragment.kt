package com.example.bhagwatgita.ui.fragments

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.bhagwatgita.R
import com.example.bhagwatgita.databinding.FragmentSavedBinding
import com.example.bhagwatgita.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {
    private lateinit var binding : FragmentSplashBinding
    private var splashHandler: Handler? = null
    private lateinit var rotationAnimator : ObjectAnimator
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        setStatusBarColor()
        binding =  FragmentSplashBinding.inflate(layoutInflater)

        splashHandler = Handler(Looper.getMainLooper())
        splashHandler?.postDelayed({
            findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
        },3000)

        // Rotate imageView by 360 degrees infinitely

        rotationAnimator = ObjectAnimator.ofFloat(binding.ivChakra, "rotation", 0f, 360f)
        rotationAnimator.duration = 1000 // Duration of the animation in milliseconds
        rotationAnimator.repeatCount = ObjectAnimator.INFINITE // Repeat the animation infinitely
        rotationAnimator.interpolator = LinearInterpolator() // Linear interpolation

        // Start the animation
//        rotationAnimator.start()
        return binding.root
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

    override fun onDestroyView() {
        super.onDestroyView()
        if(isAdded){
            splashHandler?.removeCallbacksAndMessages(null)
            splashHandler = null
        }
        rotationAnimator.cancel()
        rotationAnimator.removeAllListeners()
        rotationAnimator.removeAllUpdateListeners()
    }
}