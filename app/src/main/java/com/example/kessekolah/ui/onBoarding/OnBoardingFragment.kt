package com.example.kessekolah.ui.onBoarding

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.kessekolah.R
import com.example.kessekolah.databinding.FragmentOnBoardingBinding
import com.example.kessekolah.utils.LoginPreference

class OnBoardingFragment : Fragment() {

    private var _binding: FragmentOnBoardingBinding? = null
    private val binding get() = _binding!!

    private lateinit var pref: LoginPreference
    private var currentPage = 1
    private lateinit var onBackPressedCallback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnBoardingBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setStatusBarTextColorGray()
        setStatusBarBackgroundColorWhite()

        pref = LoginPreference(requireContext())

        binding.btnNext.setOnClickListener {
            if (currentPage < 6) {
                currentPage++
                setupOnBoardingPage(currentPage)
            } else {
                showPermissionDialog()
            }
        }
    }

    private fun showPermissionDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Internet Permission Required")
            .setMessage("This app requires permission to use the internet. Please allow internet access to continue.")
            .setPositiveButton("Allow") { _, _ ->
                checkInternetPermission()
            }
            .setNegativeButton("Exit") { _, _ ->
                requireActivity().finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun checkInternetPermission() {
        if (!isInternetAvailable(requireContext())) {
            showInternetPermissionDialog()
        } else {
            navigateToLogin()
        }
    }

    private fun showInternetPermissionDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Internet Permission Required")
            .setMessage("This app requires an active internet connection. Please enable mobile data or Wi-Fi.")
            .setPositiveButton("Settings") { _, _ ->
                startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
            }
            .setNegativeButton("Exit") { _, _ ->
                requireActivity().finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun navigateToLogin() {
        findNavController().navigate(R.id.action_onBoardingFragment_to_sectionFragment)
    }

    private fun setupOnBoardingPage(page: Int) {
        when (page) {
            1 -> {
                binding.onBoardingImageContent.setImageResource(R.drawable.image_onboarding2)
                binding.onBoardingImageContent.invalidate() // Invalidate the view
                binding.onBoardingDescription.visibility = View.VISIBLE
                binding.tvHint.visibility = View.GONE
                binding.onBoardingDescription.text = getString(R.string.hint_welcome)
                binding.onBoardingDescription.apply {
                    textSize = 31f
                    typeface = null
                }
            }
            2 -> {
                binding.onBoardingImageContent.setImageResource(R.drawable.image_onboarding)
                binding.onBoardingDescription.text = getString(R.string.onboarding_description)
                binding.onBoardingDescription.visibility = View.VISIBLE
                binding.tvHint.visibility = View.GONE
                binding.onBoardingDescription.apply {
                    textSize = 16f
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val customFont = resources.getFont(R.font.fredoka_regular)
                        typeface = customFont
                    }
                }
            }
            3 -> {
                binding.onBoardingImageContent.setImageResource(R.drawable.tampilanhome)
                binding.onBoardingDescription.visibility = View.GONE
                binding.tvHint.visibility = View.VISIBLE
                binding.tvHint.text = getString(R.string.description_home)
            }

            4 -> {
                binding.onBoardingImageContent.setImageResource(R.drawable.listmateri)
                binding.onBoardingDescription.visibility = View.GONE
                binding.tvHint.visibility = View.VISIBLE
                binding.tvHint.text = getString(R.string.description_lesson)
            }

            5 -> {
                binding.onBoardingImageContent.setImageResource(R.drawable.listvideo)
                binding.onBoardingDescription.visibility = View.GONE
                binding.tvHint.visibility = View.VISIBLE
                binding.tvHint.text = getString(R.string.description_video)
            }

            6 -> {
                binding.btnNext.text = getString(R.string.next_study)
                binding.onBoardingImageContent.setImageResource(R.drawable.listpertanyaan)
                binding.onBoardingDescription.visibility = View.GONE
                binding.tvHint.visibility = View.VISIBLE
                binding.tvHint.text = getString(R.string.description_askexpert)
            }

            else -> {
                binding.onBoardingImageContent.setImageResource(R.drawable.image_onboarding2)
                binding.onBoardingDescription.text = getString(R.string.hint_welcome)
                binding.onBoardingDescription.apply {
                    textSize = 31f
                    typeface = null
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (currentPage > 1) {
                    currentPage--
                    setupOnBoardingPage(currentPage)
                } else {
                    requireActivity().finish()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)
    }

    private fun setStatusBarTextColorGray() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val decor = activity?.window?.decorView
            @Suppress("DEPRECATION")
            decor?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    private fun setStatusBarBackgroundColorWhite() {
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), android.R.color.white)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
