package com.example.kessekolah.ui.core.beranda.referensi.editReferensi

import android.app.ProgressDialog
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.kessekolah.R
import com.example.kessekolah.data.database.MateriData
import com.example.kessekolah.databinding.FragmentEditMateriBinding
import com.example.kessekolah.databinding.FragmentEditReferensiBinding
import com.example.kessekolah.model.BookMarkViewModel
import com.example.kessekolah.model.EditMateriViewModel
import com.example.kessekolah.ui.adapter.IlusPickerAdapter
import com.example.kessekolah.viewModel.ViewModelFactoryBookMark
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class EditReferensiFragment : Fragment() {

    private var _binding: FragmentEditReferensiBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EditReferensiViewModel by viewModels()
    private val args : EditReferensiFragmentArgs by navArgs()

    private lateinit var data: MateriData
    private val listIlus = listOf(1, 2, 3, 4, 5, 6, 7, 8)
    private var numberIlus by Delegates.notNull<Int>()
    private lateinit var materiBookMarkViewModel: BookMarkViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditReferensiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val vmFactory = ViewModelFactoryBookMark.getInstance(requireActivity().application)
        materiBookMarkViewModel = ViewModelProvider(
            requireActivity(),
            vmFactory
        )[BookMarkViewModel::class.java]

        data = args.data
        numberIlus = data.dataIlus

        setupData()
        buttonClick()
        loadingHandler()
    }

    private fun setupData() = with(binding.inForm) {
        textJudulMateri.setText(data.judul)
        textTahun.setText(data.tahun)
        tvPilihIlustrasi.setText("Ilustrasi ${data.dataIlus} dipilih!")
        btnAddFile.setText(data.fileName)

        val listAdapter = IlusPickerAdapter(listIlus)
        rvIlus.layoutManager = GridLayoutManager(requireContext(), 4)
        rvIlus.adapter = listAdapter

        listAdapter.setOnItemClickCallback(object: IlusPickerAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Int) {
                numberIlus = data
                tvPilihIlustrasi.text = "Ilustrasi $data dipilih!"
            }

        })
    }

    private fun buttonClick() = with(binding){
        btnSubmit.setOnClickListener {

            val mJudul = inForm.textJudulMateri.text.toString().trim()
            val mTahun = inForm.textTahun.text.toString().trim()

            if (mJudul.isEmpty() || mTahun.isEmpty() || numberIlus == 0) {
                Toast.makeText(
                    requireContext(),
                    "Lengkapi inputan",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                viewLifecycleOwner.lifecycleScope.launch {
                    val isError = viewModel.editMateri(mJudul, mTahun, numberIlus, data.fileName.substringBeforeLast("."))
                    responseHandler(isError)
                }
            }
        }
    }

    private fun responseHandler(isError: Boolean) {
        if (isError) {
            Toast.makeText(requireContext(), "Data materi gagal dirubah", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Data materi berhasil dirubah", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
            findNavController().previousBackStackEntry?.also {
                it.savedStateHandle.set("updateBookmarks", true)
            }
        }
    }

    private fun loadingHandler() {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Uploading Data..")
        progressDialog.setCancelable(false)
        viewModel.loading.observe(viewLifecycleOwner) { loading ->
            if (loading) progressDialog.show() else progressDialog.dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}