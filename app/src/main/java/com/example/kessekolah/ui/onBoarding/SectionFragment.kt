package com.example.kessekolah.ui.onBoarding

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.kessekolah.R
import com.example.kessekolah.databinding.FragmentLoginBinding
import com.example.kessekolah.databinding.FragmentSectionBinding

class SectionFragment : Fragment() {

    private var _binding: FragmentSectionBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSectionBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        actionClick()
    }

    private fun actionClick() = with(binding.inForm) {
        btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_sectionFragment_to_loginFragment)
        }
        btnGuest.setOnClickListener {
            findNavController().navigate(R.id.action_sectionFragment_to_homeActivity)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}