package com.example.kessekolah.ui.core.profile

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.kessekolah.MainActivity
import com.example.kessekolah.R
import com.example.kessekolah.data.remote.LoginData
import com.example.kessekolah.databinding.FragmentProfileBinding
import com.example.kessekolah.utils.LoginPreference
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var preference: LoginPreference
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var dataLogin: LoginData
    private lateinit var storage: FirebaseStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preference = LoginPreference(requireContext())
        dataLogin = preference.getData()
        storage = FirebaseStorage.getInstance()
        sharedPreferences = requireActivity().getSharedPreferences("user_preferences", Context.MODE_PRIVATE)

        setupData()
        setupDarkMode()
        buttonClick()

        parentFragmentManager.setFragmentResultListener("editProfileResult", viewLifecycleOwner) { _, result ->
            val updatedName = result.getString("updatedName")
            val updatedProfilePicture = result.getString("updatedProfilePicture")

            updatedName?.let { dataLogin.name = it }
            updatedProfilePicture?.let { dataLogin.profilePicture = it }

            setupData()
        }
    }

    private fun setupData() = with(binding) {

        if (dataLogin.role == "Guru" || dataLogin.role == "Admin"){
            tvName.text = dataLogin.name
            tvEmail.text = dataLogin.email
            tvProfesi.text = dataLogin.role
        } else {
            tvName.text = getString(R.string.profile_guest_name)
            tvEmail.visibility = View.GONE
            tvProfesi.text = getString(R.string.profile_guest_profesi)
        }

        if (dataLogin.profilePicture?.isNotEmpty() == true) {
            Picasso.get().load(dataLogin.profilePicture).into(imgAvatar)
        } else {
            imgAvatar.setImageResource(R.drawable.logo_app)
        }
    }

    private fun setupDarkMode() = with(binding) {
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        btnDarkMode.isChecked = isDarkMode

        btnDarkMode.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPreferences.edit()
            editor.putBoolean("dark_mode", isChecked)
            editor.apply()

            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }


    private fun buttonClick() = with(binding) {
        if (dataLogin.role == "Guru" || dataLogin.role == "Admin"){
            btnEditProfile.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
            }
        } else {
            btnEditProfile.visibility = View.GONE
        }

        btnTentangSekolah.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_tentangSekolahFragment)
        }

        btnKeluar.setOnClickListener {
            preference.removeData()
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            requireActivity().finish()

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
