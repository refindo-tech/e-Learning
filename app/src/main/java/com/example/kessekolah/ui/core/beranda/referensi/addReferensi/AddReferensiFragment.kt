package com.example.kessekolah.ui.core.beranda.referensi.addReferensi

import android.app.ProgressDialog
import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.atwa.filepicker.core.FilePicker
import com.example.kessekolah.R
import com.example.kessekolah.databinding.FragmentAddMateriBinding
import com.example.kessekolah.databinding.FragmentAddReferensiBinding
import com.example.kessekolah.ui.adapter.IlusPickerAdapter
import com.example.kessekolah.ui.core.beranda.materi.addMateri.AddMateriViewModel
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storageMetadata
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class AddReferensiFragment : Fragment() {
    private var _binding: FragmentAddReferensiBinding? = null
    private val binding get() = _binding!!
    private val listIlus = listOf(1, 2, 3, 4, 5, 6, 7, 8)
    private var numberIlus = 0
    private var file: File? = null
    private val filePicker = FilePicker.getInstance(this)
    private val colorsIlus = arrayOf("#328F5A", "#FF6500", "#3C87F8", "#3C87F8", "#3C87F8", "#FF6500", "#3C87F8", "#328F5A")

    private val viewModel: AddReferensiViewModel by viewModels()

    private lateinit var auth: FirebaseAuth
    private val materiRef = FirebaseDatabase.getInstance().getReference("referensi")
    private val storage = FirebaseStorage.getInstance().reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddReferensiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        setData(listIlus)
        buttonAction()
        loadingHandler()
    }

    private fun setData(data: List<Int>) {
        val listAdapter = IlusPickerAdapter(data)

        with(binding.inForm) {
            rvIlus.layoutManager = GridLayoutManager(requireContext(), 4)
            rvIlus.adapter = listAdapter

            listAdapter.setOnItemClickCallback(object: IlusPickerAdapter.OnItemClickCallback {
                override fun onItemClicked(data: Int) {
                    // get data ilus, Int type
                    numberIlus = data
                    tvPilihIlustrasi.text = "Ilustrasi $data dipilih!"
                }
            })

            btnAddFile.setOnClickListener { pickFile() }

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

    private fun buttonAction() {
        with(binding) {

            topAppBar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            btnSubmit.setOnClickListener {

                val mJudul = inForm.textJudulMateri.text.toString().trim()
                val mTahun = inForm.textTahun.text.toString().trim()

                if (mJudul.isEmpty() || mTahun.isEmpty() || numberIlus == 0 || file == null) {
                    Toast.makeText(
                        requireContext(),
                        "Lengkapi inputan",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // Check file format
                    val fileExtension = file?.extension ?: ""
                    if (fileExtension != "pdf" && fileExtension != "png") {
                        Toast.makeText(
                            requireContext(),
                            "Pilih file dengan format PDF atau PNG",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        uploadData(mJudul, mTahun, file!!)
                    }
                }
            }
        }
    }

    private fun uploadData(judul: String, tahun: String, file: File) {
        viewModel.setLoading(true)

        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        // Step 1: Ambil semua data dari `materi` untuk menemukan ID tertinggi
        materiRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                var highestId = 0
                for (childSnapshot in task.result.children) {
                    val id = childSnapshot.key
                    if (id != null) {
                        val currentId = id.toIntOrNull() ?: continue
                        if (currentId > highestId) {
                            highestId = currentId
                        }
                    }
                }

                val newId = highestId + 1
                val fileExtension = file.extension
                val fileId = UUID.randomUUID().toString()
                val fileRef = storage.child("referensi/${user.uid}/$fileId.$fileExtension")

                val metadata = storageMetadata {
                    setCustomMetadata("owner", user.uid)
                }

                fileRef.putFile(Uri.fromFile(file), metadata)
                    .addOnSuccessListener {
                        fileRef.downloadUrl.addOnSuccessListener { uri ->

                            val backColorBanner = getBackColor(numberIlus)

                            // Buat Map untuk menyimpan data
                            val materiDataMap = mapOf(
                                "id" to newId,
                                "judul" to judul,
                                "tahun" to tahun,
                                "category" to "Referensi",
                                "fileName" to "$fileId.$fileExtension",
                                "fileUrl" to uri.toString(),
                                "timestamp" to getCurrentDateTime(),
                                "uid" to user.uid,
                                "dataIlus" to numberIlus,
                                "backColorBanner" to backColorBanner
                            )

                            // Simpan data menggunakan ID baru
                            materiRef.child(fileId)
                                .setValue(materiDataMap)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        viewModel.setLoading(false)
                                        Toast.makeText(requireContext(), "Materi berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                                        findNavController().navigateUp()
                                    } else {
                                        viewModel.setLoading(false)
                                        Toast.makeText(requireContext(), "Gagal menambahkan materi", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        viewModel.setLoading(false)
                        Log.e("UploadData", "Gagal mengunggah file", e)
                        Toast.makeText(requireContext(), "Gagal mengunggah file", Toast.LENGTH_SHORT).show()
                    }
            } else {
                viewModel.setLoading(false)
                Toast.makeText(requireContext(), "Gagal mendapatkan data materi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getBackColor(ilusNumber: Int): String {
        return colorsIlus.getOrElse(ilusNumber - 1) { "#3C87F8" } // Default to white if out of bounds
    }

    private fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }

    private fun pickFile() {
        filePicker.pickFile { meta ->
            val file: File? = meta?.file

            binding.inForm.btnAddFile.text = meta?.name ?: "nama null"
            this.file = file
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}