package com.example.kessekolah.ui.core.beranda.materi.searchMateri

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kessekolah.R
import com.example.kessekolah.data.database.MateriData
import com.example.kessekolah.databinding.FragmentEditProfileBinding
import com.example.kessekolah.databinding.FragmentSearchMateriBinding
import com.example.kessekolah.model.ListMateriViewModel
import com.example.kessekolah.ui.adapter.MateriListAdapter
import com.example.kessekolah.ui.core.beranda.materi.listMateri.ListMateriFragmentDirections
import com.example.kessekolah.viewModel.ViewModelFactoryBookMark
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class SearchMateriFragment : Fragment() {

    private var _binding: FragmentSearchMateriBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ListMateriViewModel
    lateinit var materiList: ArrayList<MateriData>
    private lateinit var adapter: MateriListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchMateriBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val vmFactory = ViewModelFactoryBookMark.getInstance(requireActivity().application)

        viewModel = ViewModelProvider(
            requireActivity(),
            vmFactory
        )[ListMateriViewModel::class.java]

        setupData()

    }

    private fun setupData() = with(binding) {
        adapter = MateriListAdapter()
        rvMateri.adapter = adapter
        rvMateri.layoutManager = LinearLayoutManager(requireContext())

        adapter.setOnItemClickCallback(object : MateriListAdapter.OnItemClickCallback {

            override fun onDeleteClicked(data: MateriData) {
                showDialog(data)
            }

            override fun onEditClicked(data: MateriData) {
                val action = SearchMateriFragmentDirections.actionSearchMateriFragmentToEditMateriFragment(data)
                findNavController().navigate(action)
            }

            override fun onItemClicked(data: MateriData) {
                val action = SearchMateriFragmentDirections.actionSearchMateriFragmentToFlipBookTestFragment(data)
                findNavController().navigate(action)
            }

        })

        viewModel.materiList.observe(viewLifecycleOwner) { data ->
            data?.let {
                materiList = it as ArrayList<MateriData>
                adapter.submitList(it)
            }
        }

        textJudulMateri.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)

        val searchItem: MenuItem = menu.findItem(R.id.actionSearch)
        val searchView: SearchView = searchItem.getActionView() as SearchView

        searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(msg: String): Boolean {
                filter(msg)
                return false
            }
        })
    }

    private fun showDialog(data: MateriData) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.title_dialog_delete))
            .setMessage("Materi \"${data.judul}\" akan dihapus dari database")
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.accept)) { _, _ ->
                viewModel.deleteMateri(data)
            }
            .show()
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.actionSearch -> return true
//        }
//        return super.onOptionsItemSelected(item)
//    }



    private fun filter(text: String) {
        val filteredlist: ArrayList<MateriData> = ArrayList()

        for (item in materiList) {

            if (item.judul.toLowerCase().contains(text.toLowerCase())) {

                filteredlist.add(item)
            }
        }
        if (filteredlist.isEmpty()) {
//            Toast.makeText(requireContext(), "No Data Found..", Toast.LENGTH_SHORT).show()
        } else {
            adapter.filterList(filteredlist)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}