package com.example.kessekolah.ui.core.profile.editProfile

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.kessekolah.R
import com.example.kessekolah.data.remote.LoginData
import com.example.kessekolah.databinding.FragmentEditProfileBinding
import com.example.kessekolah.utils.LoginPreference
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EditProfileViewModel by viewModels()

    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_PICK = 2
    private val REQUEST_PERMISSION = 3

    private var imageUri: Uri? = null

    private lateinit var preference: LoginPreference
    private lateinit var dataLogin: LoginData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preference = LoginPreference(requireContext())
        dataLogin = preference.getData()


        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        checkPermissions()
        buttonClick()
        setupData(dataLogin)
    }

    private fun setupData(data: LoginData) = with(binding) {
        textName.setText(data.name)
        if (data.profilePicture?.isNotEmpty() == true) {
            Picasso.get().load(dataLogin.profilePicture).into(imgProfile)
        } else {
            imgProfile.setImageResource(R.drawable.logo_app)
        }
    }

    private fun buttonClick() = with(binding) {
        imgProfile.setOnClickListener {
            showImagePickerDialog()
        }

        btnSubmit.setOnClickListener {
            val newName = textName.text.toString().trim()
            if (newName.isNotEmpty()) {
                viewModel.editProfile(
                    dataLogin.token.toString(),
                    newName,
                    imageUri
                ) { success, error ->
                    if (success) {
                        Toast.makeText(
                            requireContext(),
                            "Profile updated successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        // Pass back the updated data to ProfileFragment
                        val result = Bundle().apply {
                            putString("updatedName", newName)
                            putString("updatedProfilePicture", imageUri?.toString())
                        }
                        parentFragmentManager.setFragmentResult("editProfileResult", result)
                        findNavController().popBackStack()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Failed to update profile: $error",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.i("URI profile error", error.toString())
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    imageUri = getImageUriFromBitmap(imageBitmap)
                    binding.imgProfile.setImageBitmap(imageBitmap)
                }

                REQUEST_IMAGE_PICK -> {
                    imageUri = data?.data
                    imageUri?.let {
                        binding.imgProfile.setImageURI(it)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
