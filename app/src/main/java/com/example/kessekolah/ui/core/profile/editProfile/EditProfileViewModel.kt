package com.example.kessekolah.ui.core.profile.editProfile

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class EditProfileViewModel : ViewModel() {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
    private val storage = FirebaseStorage.getInstance()

    fun editProfile(uid: String, newName: String, newProfileImageUri: Uri?, callback: (Boolean, String?) -> Unit) {
        val userRef = database.child(uid)

        if (newProfileImageUri != null) {
            val imageFileName = UUID.randomUUID().toString()
            val storageRef = storage.reference.child("profile_images/$uid/$imageFileName")
            storageRef.putFile(newProfileImageUri)
                .continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let { throw it }
                    }
                    storageRef.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        updateUserProfile(uid, newName, downloadUri.toString(), callback)
                    } else {
                        callback(false, task.exception?.message)
                    }
                }
        } else {
            updateUserProfile(uid, newName, null, callback)
        }
    }

    private fun updateUserProfile(uid: String, newName: String, profileImageUrl: String?, callback: (Boolean, String?) -> Unit) {
        val updates = hashMapOf(
            "name" to newName
        )

        profileImageUrl?.let {
            updates["userProfilePicture"] = it
        }

        database.child(uid).updateChildren(updates as Map<String, Any>)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }
}
