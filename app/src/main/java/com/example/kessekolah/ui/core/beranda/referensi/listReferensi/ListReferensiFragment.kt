package com.example.kessekolah.ui.core.beranda.referensi.listReferensi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kessekolah.R
import com.example.kessekolah.data.database.MateriData
import com.example.kessekolah.data.remote.LoginData
import com.example.kessekolah.databinding.FragmentListReferensiBinding
import com.example.kessekolah.ui.adapter.MateriListAdapter
import com.example.kessekolah.ui.adapter.MateriListSiswaAdapter
import com.example.kessekolah.utils.LoginPreference
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ListReferensiFragment : Fragment() {

    private var _binding: FragmentListReferensiBinding? = null
    private val binding get() = _binding!!
    private lateinit var dataLogin: LoginData

    private val viewModel: ListReferensiViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListReferensiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataLogin = LoginPreference(requireContext()).getData()

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }


        if (dataLogin.role == "Guru" || dataLogin.role == "Admin") {
            itemListGuru()
        } else {
            itemListSiswa()
        }
    }

    private fun itemListGuru() {
        val adapter = MateriListAdapter()
        binding.rvReferensi.adapter = adapter
        binding.rvReferensi.layoutManager = LinearLayoutManager(requireContext())

        adapter.setOnItemClickCallback(object : MateriListAdapter.OnItemClickCallback {

            override fun onDeleteClicked(data: MateriData) {
                showDialog(data)
            }

            override fun onEditClicked(data: MateriData) {
                val action = ListReferensiFragmentDirections.actionListReferensiFragmentToEditReferensiFragment(data)
                findNavController().navigate(action)
            }

            override fun onItemClicked(data: MateriData) {
                val action = ListReferensiFragmentDirections.actionListReferensiFragmentToDetailReferensiFragment(data)
                findNavController().navigate(action)
            }

        })

        viewModel.materiList.observe(viewLifecycleOwner) { materiList ->
            viewDataEmpty(materiList.isEmpty())
            materiList?.let {
                adapter.submitList(it)
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) { loading ->
            if (loading) {
                binding.loadingIndicator.visibility = View.VISIBLE
                binding.rvReferensi.visibility = View.GONE
            } else {
                binding.loadingIndicator.visibility = View.GONE
                binding.rvReferensi.visibility = View.VISIBLE
            }
        }

        buttonCLick()
    }

    private fun itemListSiswa() {
        binding.btnTambahReferensi.visibility = View.GONE
        val adapter = MateriListSiswaAdapter()
        binding.rvReferensi.adapter = adapter
        binding.rvReferensi.layoutManager = LinearLayoutManager(requireContext())

        adapter.setOnItemClickCallback(object : MateriListSiswaAdapter.OnItemClickCallback {

            override fun onItemClicked(data: MateriData) {
                val action = ListReferensiFragmentDirections.actionListReferensiFragmentToDetailReferensiFragment(data)
                findNavController().navigate(action)

            }

        })

        viewModel.materiList.observe(viewLifecycleOwner) { materiList ->
            viewDataEmpty(materiList.isEmpty())
            materiList?.let {
                adapter.submitList(it)
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) { loading ->
            if (loading) {
                binding.loadingIndicator.visibility = View.VISIBLE
                binding.rvReferensi.visibility = View.GONE
            } else {
                binding.loadingIndicator.visibility = View.GONE
                binding.rvReferensi.visibility = View.VISIBLE
            }
        }
    }

    private fun buttonCLick() {
        with(binding) {
            btnTambahReferensi.setOnClickListener {
                findNavController().navigate(R.id.action_listReferensiFragment_to_addReferensiFragment)
            }
        }
    }

    //view handler when data empty
    private fun viewDataEmpty(isEmpty: Boolean) {
        with(binding) {
            if (isEmpty) imgDataEmpty.visibility = View.VISIBLE  else imgDataEmpty.visibility = View.GONE
        }
    }

    private fun showDialog(data: MateriData) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.title_dialog_delete))
            .setMessage("Lesson \"${data.judul}\" will be deleted from database")
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.accept)) { _, _ ->
                viewModel.deleteMateri(data)
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}