package com.example.kessekolah.ui.core.beranda.materi.detailMateri

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kessekolah.R
import com.example.kessekolah.data.database.MateriData
import com.example.kessekolah.data.remote.LoginData
import com.example.kessekolah.databinding.FragmentDetailMateriBinding
import com.example.kessekolah.model.ListMateriViewModel
import com.example.kessekolah.ui.adapter.BannerDetailMateriAdapter
import com.example.kessekolah.utils.LoginPreference
import com.example.kessekolah.viewModel.ViewModelFactoryBookMark
import com.rajat.pdfviewer.PdfRendererView

class DetailMateriFragment : Fragment() {

    private var _binding: FragmentDetailMateriBinding? = null
    private val binding get() = _binding!!
    private lateinit var materiListAdapterCore: BannerDetailMateriAdapter
    private lateinit var viewModel: ListMateriViewModel

    private var materiBookMark: MateriData? = null
    private var isFavorite: Boolean = false
    private lateinit var dataLogin: LoginData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailMateriBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        val vmFactory = ViewModelFactoryBookMark.getInstance(requireActivity().application)

        dataLogin = LoginPreference(requireContext()).getData()

        viewModel = ViewModelProvider(
            requireActivity(),
            vmFactory
        )[ListMateriViewModel::class.java]

        val materiData = arguments?.getParcelable<MateriData>("data")
        if (materiData != null) {
            materiBookMark = materiData
            setupBanner(materiData)
            displayPdf(materiData.fileUrl)
        } else {
            Toast.makeText(requireContext(), "Data not found", Toast.LENGTH_SHORT).show()
        }

        viewModel.getFavoriteData().observe(viewLifecycleOwner) { materiBM ->
            if (materiBM != null) {
                isFavorite = materiBM.any { it.judul == materiData?.judul }
                val bookmarkMenuItem = binding.topAppBar.menu.findItem(R.id.bookmark_bar)
                bookmarkMenuItem.setIcon(if (isFavorite) R.drawable.baseline_bookmark_24 else R.drawable.baseline_bookmark_border_24)
            }
        }

        binding.topAppBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.bookmark_bar -> {
                    if (!isFavorite) {
                        isFavorite = true
                        item.setIcon(R.drawable.baseline_bookmark_24)
//                        viewModel.insertMateriBookMark(materiBookMark!!)
                        viewModel.addBookmarkToFirebase(dataLogin.token!!, materiBookMark!!)
                        Toast.makeText(requireContext(), "Successfully added to Bookmark", Toast.LENGTH_SHORT).show()
                    } else {
                        isFavorite = false
                        item.setIcon(R.drawable.baseline_bookmark_border_24)
//                        viewModel.deleteMateriBookMark(materiBookMark!!.id)
                        Toast.makeText(requireContext(), "Deleted from Bookmarks", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                else -> false
            }
        }

    }

    private fun setupBanner(materiData: MateriData) {
        materiListAdapterCore = BannerDetailMateriAdapter()
        binding.rvBanner.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvBanner.adapter = materiListAdapterCore

        materiListAdapterCore.submitList(listOf(materiData))
    }

    private fun displayPdf(fileUrl: String) {
        binding.loadingProgressBar.visibility = View.VISIBLE

        binding.pdfView.statusListener = object : PdfRendererView.StatusCallBack {
            override fun onPdfLoadStart() {
                Log.i("statusCallBack", "onPdfLoadStart")
                binding.loadingProgressBar.visibility = View.VISIBLE
            }

            override fun onPdfLoadProgress(
                progress: Int,
                downloadedBytes: Long,
                totalBytes: Long?
            ) {
                //Download is in progress
            }

            override fun onPdfLoadSuccess(absolutePath: String) {
                Log.i("statusCallBack", "onPdfLoadSuccess")
                binding.loadingProgressBar.visibility = View.GONE
            }

            override fun onError(error: Throwable) {
                Log.i("statusCallBack", "onError")
                binding.loadingProgressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Failed to load PDF", Toast.LENGTH_SHORT).show()
            }

            override fun onPageChanged(currentPage: Int, totalPage: Int) {
                //Page change. Not require
            }
        }
        binding.pdfView.initWithUrl(
            url = fileUrl,
            lifecycleCoroutineScope = lifecycleScope,
            lifecycle = lifecycle
        )
        binding.pdfView.jumpToPage(1)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.nav_bar_bookmark, menu)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

