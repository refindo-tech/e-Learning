package com.example.kessekolah.ui.core.bookmark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kessekolah.R
import com.example.kessekolah.data.database.MateriData
import com.example.kessekolah.data.remote.LoginData
import com.example.kessekolah.databinding.FragmentBookMarkBinding
import com.example.kessekolah.model.BookMarkViewModel
import com.example.kessekolah.ui.adapter.BookMarkAdapter
import com.example.kessekolah.utils.LoginPreference
import com.example.kessekolah.viewModel.ViewModelFactoryBookMark
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class BookMarkFragment : Fragment() {

    private var _binding: FragmentBookMarkBinding? = null
    private val binding get() = _binding!!
    private lateinit var materiBookMarkViewModel: BookMarkViewModel
    private lateinit var dataLogin: LoginData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookMarkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val vmFactory = ViewModelFactoryBookMark.getInstance(requireActivity().application)
        materiBookMarkViewModel = ViewModelProvider(
            requireActivity(),
            vmFactory
        )[BookMarkViewModel::class.java]

        dataLogin = LoginPreference(requireContext()).getData()

        showRv()
    }

    private fun showRv() {
        val adapter = BookMarkAdapter()
        binding.rvMateriBookmark.adapter = adapter
        binding.rvMateriBookmark.layoutManager = LinearLayoutManager(requireContext())

        adapter.setOnItemClickCallback(object : BookMarkAdapter.OnItemClickCallback {

            override fun onDeleteClicked(data: MateriData) {
                showDialog(data)
            }

            override fun onItemClicked(data: MateriData) {
                val action = BookMarkFragmentDirections.actionBookMarkFragmentToFlipBookTestFragment(data)
                findNavController().navigate(action)
            }

        })

        materiBookMarkViewModel.getAllBookmarks(dataLogin.token!!).observe(viewLifecycleOwner) { listUser ->
            viewDataEmpty(listUser.isEmpty())
            listUser?.let {
                adapter.submitList(it)
            }
        }


        materiBookMarkViewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            if (loading) {
                binding.loadingIndicator.visibility = View.VISIBLE
                binding.rvMateriBookmark.visibility = View.GONE
            } else {
                binding.loadingIndicator.visibility = View.GONE
                binding.rvMateriBookmark.visibility = View.VISIBLE
            }
        }
    }

    private fun showDialog(data: MateriData) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.title_dialog_delete))
            .setMessage("Materi \"${data.judul}\" akan dihapus dari database")
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.accept)) { _, _ ->
                materiBookMarkViewModel.removeBookmarkFromFirebase(dataLogin.token!!, data.judul)
                val adapter = binding.rvMateriBookmark.adapter as BookMarkAdapter
                val newList = materiBookMarkViewModel.bookmarks.value?.filter { it.judul != data.judul }
                adapter.submitList(newList)
                viewDataEmpty(newList.isNullOrEmpty())
            }
            .show()
    }


    private fun viewDataEmpty(isEmpty: Boolean) {
        with(binding) {
            if (isEmpty) imgDataEmpty.visibility = View.VISIBLE else imgDataEmpty.visibility = View.GONE
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
