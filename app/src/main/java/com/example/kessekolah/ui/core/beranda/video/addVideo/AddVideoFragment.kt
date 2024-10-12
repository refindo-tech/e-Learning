package com.example.kessekolah.ui.core.beranda.video.addVideo

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.kessekolah.R
import com.example.kessekolah.data.database.VideoList
import com.example.kessekolah.databinding.FragmentAddVideoBinding
import com.example.kessekolah.ui.adapter.IlusPickerAdapter
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class AddVideoFragment : Fragment() {
    private var _binding: FragmentAddVideoBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private val listIlus = listOf(1, 2, 3, 4, 5, 6, 7, 8)
    private var numberIlus = 0
    private var selectedCategory: String? = null
    private val colorsIlus = arrayOf("#328F5A", "#FF6500", "#3C87F8", "#3C87F8", "#3C87F8", "#FF6500", "#3C87F8", "#328F5A")

    private val viewModel: AddVideoViewModel by viewModels()
    private val args: AddVideoFragmentArgs by navArgs()
    private var videoData: VideoList? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddVideoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        videoData = args.data
        setData(listIlus)
        buttonAction()
        loadingHandler()

        if (videoData != null) {
            setupEditMode()
        } else {
            setupAddMode()
        }

    }

    private fun setupEditMode() {
        binding.topAppBar.title = getString(R.string.edit_video_title)
        binding.btnSubmit.text = getString(R.string.btn_update)
        with(binding.videoForm) {
            textJudulMateri.setText(videoData?.judul)
            layoutURL.editText?.setText(videoData?.videoUrl)
            selectedCategory = videoData?.category
            selectCategoryChip(videoData?.category)
            numberIlus = videoData?.dataIlus ?: 0
            tvPilihIlustrasi.text = "Ilustrasi $numberIlus dipilih!"
        }
    }

    private fun setupAddMode() {
        binding.topAppBar.title = getString(R.string.add_video_title)
        binding.btnSubmit.text = getString(R.string.submit)
    }

    private fun selectCategoryChip(category: String?) {
        category?.let {
            for (i in 0 until  binding.videoForm.cgVideo.childCount) {
                val chip = binding.videoForm.cgVideo.getChildAt(i) as? Chip
                if (chip?.text.toString() == it) {
                    if (chip != null) {
                        chip.isChecked = true
                    }
                    break
                }
            }
        }
    }


    private fun setData(data: List<Int>) {
        val listAdapter = IlusPickerAdapter(data)
        with(binding.videoForm) {
            rvIlus.layoutManager = GridLayoutManager(requireContext(), 4)
            rvIlus.adapter = listAdapter

            listAdapter.setOnItemClickCallback(object : IlusPickerAdapter.OnItemClickCallback {
                override fun onItemClicked(data: Int) {
                    numberIlus = data
                    tvPilihIlustrasi.text = "Ilustrasi $data dipilih!"
                }
            })

            cgVideo.setOnCheckedStateChangeListener { group, checkedIds ->
                val selectedChipId = checkedIds.firstOrNull()
                val selectedChip = selectedChipId?.let { group.findViewById<Chip>(it) }
                selectedCategory = selectedChip?.text?.toString()
            }
        }
    }

    private fun buttonAction() {
        with(binding) {
            topAppBar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            btnSubmit.setOnClickListener {
                val videoId = UUID.randomUUID().toString()
                val videoTitle = videoForm.textJudulMateri.text.toString()
                val videoUrl = videoForm.layoutURL.editText?.text.toString()
                val category = selectedCategory
                val ilusNumber = numberIlus
                val backColor = getBackColor(ilusNumber)
                val currentDateTime = getCurrentDateTime()
                val uid = auth.currentUser?.uid

                if (videoTitle.isNotEmpty() && videoUrl.isNotEmpty() && category != null && ilusNumber > 0) {
                    if (videoData == null){
                        viewModel.addVideo(
                            videoId = videoId,
                            uid = uid ?: "",
                            judul = videoTitle,
                            category = category,
                            videoUrl = videoUrl,
                            timestamp = currentDateTime,
                            dataIlus = ilusNumber,
                            backColorBanner = backColor
                        )
                    } else {
                        viewModel.addVideo(
                            videoId = videoData!!.videoId,
                            uid = videoData!!.uid,
                            judul = videoTitle,
                            category = category,
                            videoUrl = videoUrl,
                            timestamp = currentDateTime,
                            dataIlus = ilusNumber,
                            backColorBanner = backColor
                        )
                    }

                } else {
                    Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
                }
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
        viewModel.messageText.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            if (message == "Berhasil Menambahkan Video") {
                findNavController().navigateUp()
            }
        }
    }

    private fun getBackColor(ilusNumber: Int): String {
        return colorsIlus.getOrElse(ilusNumber - 1) { "#3C87F8" }
    }

    private fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}