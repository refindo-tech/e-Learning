package com.example.kessekolah.ui.core.beranda.forum.addQuestion

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.kessekolah.databinding.FragmentAddQuestionBinding
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class AddQuestionFragment : Fragment() {

    private var _binding: FragmentAddQuestionBinding? = null
    private val binding get() = _binding!!
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_PICK = 2
    private val REQUEST_PERMISSION = 3

    private var imageUri: Uri? = null

    private val viewModel: AddQuestionViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddQuestionBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermissions()
        buttonClick()
        loadingHandler()
    }

    private fun buttonClick() = with(binding) {
        topAppBar.setNavigationOnClickListener{
            findNavController().popBackStack()
        }
        inForm.imgAddQuestion.setOnClickListener {
            showImagePickerDialog()
        }

        btnSubmit.setOnClickListener {
            val guestId = "guest-${UUID.randomUUID()}"
            val guestName = inForm.etAddName.text.toString().trim()
            val question = inForm.etAddQuestion.text.toString().trim()
            val desc = inForm.etAddDesc.text.toString().trim()
            val currentDateTime = getCurrentDateTime()

            if (guestName.isEmpty() || question.isEmpty()){
                Toast.makeText(
                    requireContext(),
                    "Lengkapi inputan",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else {
                viewModel.addQuestion(
                    guestId = guestId,
                    name = guestName,
                    question = question,
                    description = desc,
                    timestamp = currentDateTime,
                    imageUri = imageUri
                )
            }
        }
    }

    private fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }


    private fun showImagePickerDialog() {
        val options = arrayOf("Camera", "Gallery")
        AlertDialog.Builder(requireContext())
            .setTitle("Choose an option")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> dispatchTakePictureIntent()
                    1 -> dispatchPickPictureIntent()
                }
            }
            .show()
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    private fun dispatchPickPictureIntent() {
        val pickPictureIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickPictureIntent, REQUEST_IMAGE_PICK)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    imageUri = getImageUriFromBitmap(imageBitmap)
                    binding.inForm.imgAddQuestion.setImageBitmap(imageBitmap)
                }

                REQUEST_IMAGE_PICK -> {
                    imageUri = data?.data
                    imageUri?.let {
                        binding.inForm.imgAddQuestion.setImageURI(it)
                    }
                }
            }
        }
    }

    private fun getImageUriFromBitmap(bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            requireActivity().contentResolver,
            bitmap,
            "ProfilePicture",
            null
        )
        return Uri.parse(path)
    }

    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(
                requireContext(),
                it
            ) != PackageManager.PERMISSION_GRANTED
        }
        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                permissionsToRequest.toTypedArray(),
                REQUEST_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // Permissions granted
            } else {
                // Permissions denied
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
            if (message == "Berhasil Mengirim Pertanyaan") {
                findNavController().navigateUp()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}