package com.example.kessekolah.ui.splashScreen

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.kessekolah.R
import com.example.kessekolah.data.remote.LoginData
import com.example.kessekolah.utils.LoginPreference


class SplashScreenFragment : Fragment() {

    private lateinit var pref: LoginPreference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pref = LoginPreference(requireContext())
        val savedData = pref.getData()

        setFullScreen()

        Handler().postDelayed({
            lifecycleScope.launchWhenCreated {
                if (savedData.token?.isNotEmpty() == true && savedData.email?.isNotEmpty() == true) {
                    redirectUserToHomeScreen(savedData)
                } else {
                    redirectToLoginScreen()
                }

                //test core page
//                findNavController().navigate(R.id.action_splashScreenFragment_to_homeActivity)

                resetFullScreen()
            }
        }, MILISECON.toLong())

    }

    private fun redirectUserToHomeScreen(savedData: LoginData) {
        val bundle = Bundle().apply {savedData}
        findNavController().navigate(
            R.id.action_splashScreenFragment_to_homeActivity,
            bundle
        )
        requireActivity().finish()
    }

    private fun redirectToLoginScreen() {
        lifecycleScope.launchWhenCreated {
            findNavController().navigate(R.id.action_splashScreenFragment_to_onBoardingFragment)
        }
    }


    private fun setFullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity?.window?.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            activity?.window?.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    private fun resetFullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity?.window?.insetsController?.show(WindowInsets.Type.statusBars())
        } else {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }


    companion object{
        const val TAG = "SplashScreenFragment"
        const val MILISECON = 3000.0
    }

}