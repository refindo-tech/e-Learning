package com.example.kessekolah.ui.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.kessekolah.R
import com.example.kessekolah.databinding.FragmentTentangSekolahBinding

class TentangSekolahFragment : Fragment() {

    private var _binding: FragmentTentangSekolahBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTentangSekolahBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tentangSekolahDescriptions = resources.getStringArray(R.array.tentang_sekolah_deskripsi)
        binding.tentangSekolahDescription.text = tentangSekolahDescriptions.joinToString("\n\n")

        binding.btnExit.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}