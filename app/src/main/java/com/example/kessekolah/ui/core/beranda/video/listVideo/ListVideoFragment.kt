package com.example.kessekolah.ui.core.beranda.video.listVideo

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kessekolah.R
import com.example.kessekolah.data.database.VideoList
import com.example.kessekolah.data.remote.LoginData
import com.example.kessekolah.databinding.FragmentListVideoBinding
import com.example.kessekolah.ui.adapter.MateriListAdapter
import com.example.kessekolah.ui.adapter.MateriListSiswaAdapter
import com.example.kessekolah.ui.adapter.VideoListAdapter
import com.example.kessekolah.ui.adapter.VideoListSiswaAdapter
import com.example.kessekolah.utils.LoginPreference
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ListVideoFragment : Fragment() {

    private var _binding: FragmentListVideoBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ListVideoViewModel by viewModels()
    private lateinit var dataLogin: LoginData


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentListVideoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataLogin = LoginPreference(requireContext()).getData()
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.chipdefault.isChecked = true

        @Suppress("DEPRECATION")
        binding.chipGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.chip1 -> filterMateriByCategory(resources.getString(R.string.ch_materi_1))
                R.id.chip2 -> filterMateriByCategory(resources.getString(R.string.ch_materi_2))
                R.id.chip3 -> filterMateriByCategory(resources.getString(R.string.ch_materi_3))
                R.id.chip4 -> filterMateriByCategory(resources.getString(R.string.ch_materi_4))
                R.id.chip5 -> filterMateriByCategory(resources.getString(R.string.ch_materi_5))
                R.id.chip6 -> filterMateriByCategory(resources.getString(R.string.ch_materi_6))
                R.id.chip7 -> filterMateriByCategory(resources.getString(R.string.ch_materi_7))
                R.id.chip8 -> filterMateriByCategory(resources.getString(R.string.ch_materi_8))
                R.id.chip9 -> filterMateriByCategory(resources.getString(R.string.ch_materi_9))
                R.id.chip10 -> filterMateriByCategory(resources.getString(R.string.ch_materi_10))
                R.id.chip11 -> filterMateriByCategory(resources.getString(R.string.ch_materi_11))
                R.id.chip12 -> filterMateriByCategory(resources.getString(R.string.ch_materi_12))
                R.id.chip13 -> filterMateriByCategory(resources.getString(R.string.ch_materi_13))
                R.id.chip14 -> filterMateriByCategory(resources.getString(R.string.ch_materi_14))
                R.id.chip15 -> filterMateriByCategory(resources.getString(R.string.ch_materi_15))
                R.id.chip16 -> filterMateriByCategory(resources.getString(R.string.ch_materi_16))
                R.id.chip17 -> filterMateriByCategory(resources.getString(R.string.ch_materi_17))
                R.id.chip18 -> filterMateriByCategory(resources.getString(R.string.ch_materi_18))
                R.id.chip19 -> filterMateriByCategory(resources.getString(R.string.ch_materi_19))
                R.id.chip20 -> filterMateriByCategory(resources.getString(R.string.ch_materi_20))
                R.id.chip21 -> filterMateriByCategory(resources.getString(R.string.ch_materi_21))
                R.id.chip22 -> filterMateriByCategory(resources.getString(R.string.ch_materi_22))
                R.id.chip23 -> filterMateriByCategory(resources.getString(R.string.ch_materi_23))
                else -> showAllMateri()
            }
        }

        if (dataLogin.role == "Guru" || dataLogin.role == "Admin") {
            itemListGuru()
        } else {
            itemListSiswa()
            binding.btnTambahMateri.visibility = View.GONE
        }
    }

    private fun itemListGuru() {
        val adapter = VideoListAdapter()
        binding.rvMateri.adapter = adapter
        binding.rvMateri.layoutManager = LinearLayoutManager(requireContext())

        adapter.setOnItemClickCallback(object : VideoListAdapter.OnItemClickCallback {

            override fun onDeleteClicked(data: VideoList) {
                showDialog(data)
            }

            override fun onEditClicked(data: VideoList) {
                val action = ListVideoFragmentDirections.actionListVideoFragmentToAddVideoFragment(data)
                findNavController().navigate(action)
            }

            override fun onItemClicked(data: VideoList) {
                val action = ListVideoFragmentDirections.actionListVideoFragmentToVideoPlayerFragment(data)
                findNavController().navigate(action)
            }

        })

        viewModel.videoList.observe(viewLifecycleOwner) { videoList ->
            viewDataEmpty(videoList.isEmpty())
            videoList?.let {
                adapter.submitList(it)
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) { loading ->
            if (loading) {
                binding.loadingIndicator.visibility = View.VISIBLE
                binding.rvMateri.visibility = View.GONE
            } else {
                binding.loadingIndicator.visibility = View.GONE
                binding.rvMateri.visibility = View.VISIBLE
            }
        }

        buttonCLick()
    }

    private fun itemListSiswa() {
        val adapter = VideoListSiswaAdapter()
        binding.rvMateri.adapter = adapter
        binding.rvMateri.layoutManager = LinearLayoutManager(requireContext())

        adapter.setOnItemClickCallback(object : VideoListSiswaAdapter.OnItemClickCallback {

            override fun onItemClicked(data: VideoList) {
                val action = ListVideoFragmentDirections.actionListVideoFragmentToVideoPlayerFragment(data)
                findNavController().navigate(action)

            }

        })

        viewModel.videoList.observe(viewLifecycleOwner) { videoList ->
            viewDataEmpty(videoList.isEmpty())
            videoList?.let {
                adapter.submitList(it)
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) { loading ->
            if (loading) {
                binding.loadingIndicator.visibility = View.VISIBLE
                binding.rvMateri.visibility = View.GONE
            } else {
                binding.loadingIndicator.visibility = View.GONE
                binding.rvMateri.visibility = View.VISIBLE
            }
        }
    }

    private fun buttonCLick() {
        with(binding) {
            with(binding) {
                btnTambahMateri.setOnClickListener {
                    val action = ListVideoFragmentDirections.actionListVideoFragmentToAddVideoFragment(null)
                    findNavController().navigate(action)
                }
            }
        }
    }

    //view handler when data empty
    private fun viewDataEmpty(isEmpty: Boolean) {
        with(binding) {
            if (isEmpty) imgDataEmpty.visibility = View.VISIBLE  else imgDataEmpty.visibility = View.GONE
        }
    }

    private fun showDialog(data: VideoList) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.title_dialog_video_delete))
            .setMessage("Video \"${data.judul}\" akan dihapus dari database")
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.accept)) { _, _ ->
                viewModel.deleteVideo(data)
            }
            .show()
    }

    private fun filterMateriByCategory(category: String) {
        viewModel.videoList.observe(viewLifecycleOwner) { videoList ->
            val filteredList = videoList.filter { it.category == category }
            viewDataEmpty(filteredList.isEmpty())
            if (dataLogin.role == "Guru" || dataLogin.role == "Admin"){
                val adapter = binding.rvMateri.adapter as VideoListAdapter
                adapter.submitList(filteredList)
            } else {
                val adapter = binding.rvMateri.adapter as VideoListSiswaAdapter
                adapter.submitList(filteredList)
            }

        }
    }

    private fun showAllMateri() {
        viewModel.videoList.observe(viewLifecycleOwner) { videoList ->
            viewDataEmpty(videoList.isEmpty())
            if (dataLogin.role == "Guru" || dataLogin.role == "Admin"){
                val adapter = binding.rvMateri.adapter as VideoListAdapter
                adapter.submitList(videoList)
            } else {
                val adapter = binding.rvMateri.adapter as VideoListSiswaAdapter
                adapter.submitList(videoList)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}