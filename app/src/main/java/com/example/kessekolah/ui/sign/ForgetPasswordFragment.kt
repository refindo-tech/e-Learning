package com.example.kessekolah.ui.sign

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.kessekolah.R
import com.example.kessekolah.databinding.FragmentForgetPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import kotlin.properties.Delegates

class ForgetPasswordFragment : Fragment() {

    private var _binding: FragmentForgetPasswordBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth
    private var usernameErrorData by Delegates.notNull<Boolean>()
    private lateinit var successDialog: Dialog
    private lateinit var progressBar: ProgressBar
    private lateinit var doneLogo: ImageView
    private lateinit var successTextView: TextView

    init {
        usernameErrorData = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentForgetPasswordBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.textUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val emailPattern = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
                when {
                    !emailPattern.matcher(s!!).matches() -> {
                        binding.usernameError.text = getString(R.string.email_format_error)
                        binding.usernameError.alpha = 1F
                        usernameErrorData = true
                    }
                    s.contains(" ") -> {
                        binding.usernameError.text = getString(R.string.username_space)
                        binding.usernameError.alpha = 1F
                        usernameErrorData = true
                    }
                    s.toString().trim().isEmpty() -> {
                        binding.usernameError.alpha = 1F
                        usernameErrorData = true
                    }
                    else -> {
                        binding.usernameError.alpha = 0F
                        usernameErrorData = false
                    }
                }
                binding.btnResetPassword.isEnabled = !usernameErrorData
                binding.successForgotPassword.visibility = View.GONE // Menyembunyikan successForgotPassword saat teks diubah
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnResetPassword.setOnClickListener {
            val email = binding.textUsername.text.toString().trim()

            showLoadingDialog()
            checkEmailExists(email)
        }
    }

    private fun checkEmailExists(email: String) {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")

        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (userSnapshot in dataSnapshot.children) {
                        val userId = userSnapshot.key
                        sendPasswordResetEmail(email, userId)
                        return
                    }
                } else {
                    showErrorDialog(getString(R.string.user_has_not_found), true)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                showErrorDialog(getString(R.string.error_reset_pass))
            }
        })
    }

    private fun sendPasswordResetEmail(email: String, userId: String?) {
        userId?.let {
            firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    showSuccessDialog(getString(R.string.success_forgot_passwordd))
                    binding.successForgotPassword.visibility = View.VISIBLE
                    binding.successForgotPassword.text = getString(R.string.success_forgot_password)
                }
                .addOnFailureListener {
                    showErrorDialog(getString(R.string.error_reset_pass))
                }
        } ?: run {
            showErrorDialog(getString(R.string.error_reset_pass))
        }
    }

    private fun showLoadingDialog() {
        successDialog = Dialog(requireContext()).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.loading_effect_layout)
            setCancelable(false)

            progressBar = findViewById(R.id.progressBar)
            doneLogo = findViewById(R.id.done_logo)
            successTextView = findViewById(R.id.successTextView)

            progressBar.visibility = View.VISIBLE
            doneLogo.visibility = View.GONE
            successTextView.visibility = View.GONE

            show()

            window?.setLayout(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            window?.setBackgroundDrawable(
                ContextCompat.getDrawable(requireContext(), R.drawable.pop_out_message)
            )
        }
    }

    private fun showSuccessDialog(message: String) {
        updateDialog(
            iconResId = R.drawable.baseline_done_all_24,
            message = message
        )
    }

    private fun showErrorDialog(message: String, showOnSuccessForgotPassword: Boolean = false) {
        if (showOnSuccessForgotPassword) {
            binding.successForgotPassword.visibility = View.VISIBLE
            binding.successForgotPassword.text = message
        }
        updateDialog(
            iconResId = R.drawable.baseline_report_gmailerrorred_24,
            message = message
        )
    }

    private fun updateDialog(iconResId: Int, message: String) {
        if (::successDialog.isInitialized && successDialog.isShowing) {
            progressBar.visibility = View.GONE
            doneLogo.setImageResource(iconResId)
            successTextView.text = message
            doneLogo.visibility = View.VISIBLE
            successTextView.visibility = View.VISIBLE

            lifecycleScope.launch {
                delay(4000)
                if (successDialog.isShowing) {
                    successDialog.dismiss()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
