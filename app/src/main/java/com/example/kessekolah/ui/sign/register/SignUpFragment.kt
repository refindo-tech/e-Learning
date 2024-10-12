package com.example.kessekolah.ui.sign.register

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.kessekolah.R
import com.example.kessekolah.data.database.User
import com.example.kessekolah.data.repo.AuthRepository
import com.example.kessekolah.data.response.ResponseMessage
import com.example.kessekolah.databinding.FragmentSignUpBinding
import com.example.kessekolah.model.SignUpViewModel
import com.example.kessekolah.utils.LoginPreference
import com.example.kessekolah.viewModel.ViewModelFactorySign
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern
import kotlin.properties.Delegates

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance().reference
    private lateinit var signUpViewModel: SignUpViewModel
    private val USERS_NODE = "users"
    private val USER_ID_COUNTER_NODE = "user_id_counter"
    private var selectedRole: String = ""
    private lateinit var roleItems: Array<String>

    private var emailErrorData by Delegates.notNull<Boolean>()
    private var usernameErrorData by Delegates.notNull<Boolean>()
    private var peranErrorData by Delegates.notNull<Boolean>()
    private var passwordErrorData by Delegates.notNull<Boolean>()
    private var isEmailExist = false

    private lateinit var successDialog: Dialog
    private lateinit var progressBar: ProgressBar
    private lateinit var doneLogo: ImageView
    private lateinit var successTextView: TextView

    init {
        emailErrorData = true
        usernameErrorData = true
        peranErrorData = true
        passwordErrorData = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        roleItems = resources.getStringArray(R.array.role_items)
        val application = requireActivity().application
        val firebaseAuth = FirebaseAuth.getInstance()
        val authPreference = LoginPreference(requireContext())
        val authRepository = AuthRepository(application, authPreference, firebaseAuth)
        val vmFactory = ViewModelFactorySign.getInstance(application, authRepository, firebaseAuth)
        val adapterProvinsi = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, roleItems)
        binding.inForm.spinPeran.setAdapter(adapterProvinsi)

        signUpViewModel = ViewModelProvider(
            requireActivity(),
            vmFactory
        )[SignUpViewModel::class.java]

        auth = FirebaseAuth.getInstance()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        binding.inForm.btnLoginPage.setOnClickListener {
            findNavController().navigate(
                R.id.action_signUpFragment_to_loginFragment
            )
        }
        binding.inForm.btnSignUp.isEnabled = false

        textListener()
        buttonClick()
        roleSpin()
    }

    private fun roleSpin() {
        with(binding.inForm) {
            spinPeran.setOnItemClickListener { _, _, position, _ ->
                selectedRole = roleItems[position]
                peranErrorData = selectedRole.isEmpty()
                roleEmpty.alpha = if (peranErrorData) 1F else 0F
                btnSignUp.isEnabled = !(emailErrorData || usernameErrorData || passwordErrorData || peranErrorData)
            }
        }
    }

    private fun textListener() {
        with(binding.inForm) {
            textEmail.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val emailPattern = Pattern.compile(
                        "[a-zA-Z0-9._-]+@[a-zA-Z]+\\.[a-zA-Z]+(\\.[a-zA-Z]+)?"
                    )
                    val email = s.toString().trim()

                    emailErrorData = when {
                        email.isEmpty() -> {
                            emailEmpty.alpha = 1F
                            emailFormatError.alpha = 0F
                            emailHasSpace.alpha = 0F
                            true
                        }
                        email.contains(" ") -> {
                            emailEmpty.alpha = 0F
                            emailFormatError.alpha = 0F
                            emailHasSpace.alpha = 1F
                            true
                        }
                        !emailPattern.matcher(email).matches() -> {
                            emailEmpty.alpha = 0F
                            emailFormatError.alpha = 1F
                            emailHasSpace.alpha = 0F
                            true
                        }
                        else -> {
                            emailEmpty.alpha = 0F
                            emailFormatError.alpha = 0F
                            emailHasSpace.alpha = 0F
                            false
                        }
                    }

                    btnSignUp.isEnabled = !(emailErrorData || usernameErrorData || passwordErrorData || peranErrorData || isEmailExist)
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            textUsername.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    usernameErrorData = s.toString().trim().isEmpty()
                    binding.inForm.usernameEmpty.alpha = if (usernameErrorData) 1F else 0F
                    btnSignUp.isEnabled = !(emailErrorData || usernameErrorData || passwordErrorData || peranErrorData)
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            textPassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    passwordErrorData = s.toString().length < 8 || s.toString().trim().isEmpty()
                    binding.inForm.passwordError.alpha = if (passwordErrorData) 1F else 0F
                    btnSignUp.isEnabled = !(emailErrorData || usernameErrorData || passwordErrorData || peranErrorData)
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    private fun buttonClick() {
        with(binding.inForm) {
            btnSignUp.setOnClickListener {
                val email = textEmail.text.toString().trim()
                val username = textUsername.text.toString().trim()
                val password = textPassword.text.toString().trim()

                showLoadingDialog()
                signUpViewModel.checkEmailExists(email) { isEmailExists ->
                    if (isEmailExists) {
                        showErrorDialog(getString(R.string.email_has_used))
                        emailHasUsed.alpha = 1F
                        btnSignUp.isEnabled = false
                    } else {
                        isEmailExist = false
                        signUpViewModel.insertUser(email, password).observe(viewLifecycleOwner) { response ->
                            when (response) {
                                is ResponseMessage.Loading -> {
                                    showLoading()
                                }
                                is ResponseMessage.Success -> {
                                    database.child(USER_ID_COUNTER_NODE).child("counter").addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                                            val counter = dataSnapshot.getValue(Long::class.java) ?: 0
                                            val newUserId = counter + 1
                                            val currentUserUid = auth.currentUser?.uid
                                            if (currentUserUid != null) {
                                                val user = User(
                                                    id = newUserId.toInt(),
                                                    name = username,
                                                    email = email,
                                                    userProfilePicture = "",
                                                    role = selectedRole,
                                                    uid = currentUserUid,
                                                    createdAt = getCurrentDateTime()
                                                )
                                                database.child(USERS_NODE).child(currentUserUid)
                                                    .setValue(user).addOnCompleteListener { task ->
                                                        if (task.isSuccessful) {
                                                            database.child(USER_ID_COUNTER_NODE).child("counter")
                                                                .setValue(newUserId)
                                                            showSuccessDialog(getString(R.string.success_create_account))
                                                        } else {
                                                            showErrorDialog(getString(R.string.error_register))
                                                        }
                                                    }
                                            }
                                        }

                                        override fun onCancelled(databaseError: DatabaseError) {
                                            showErrorDialog(getString(R.string.error_register))
                                        }
                                    })
                                }
                                is ResponseMessage.Error -> {
                                    val errorMessage = when (response.message) {
                                        "The email address is badly formatted" -> getString(R.string.email_format_error)
                                        "The email address is already in use by another account." -> getString(R.string.email_has_used)
                                        else -> getString(R.string.error_register)
                                    }
                                    showErrorDialog(errorMessage)
                                    Log.d("OnErrorRegister: ", "response: ${response.message}")
                                }
                            }
                        }
                    }
                }
            }

            btnLoginPage.setOnClickListener {
                findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
            }
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

    private fun showErrorDialog(message: String) {
        successDialog.dismiss()
        successDialog = Dialog(requireContext()).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.loading_effect_layout)
            setCancelable(false)

            progressBar = findViewById(R.id.progressBar)
            doneLogo = findViewById(R.id.done_logo)
            successTextView = findViewById(R.id.successTextView)

            progressBar.visibility = View.GONE
            doneLogo.setImageResource(R.drawable.baseline_report_gmailerrorred_24)
            successTextView.text = message
            doneLogo.visibility = View.VISIBLE
            successTextView.visibility = View.VISIBLE

            show()

            window?.setLayout(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            window?.setBackgroundDrawable(
                ContextCompat.getDrawable(requireContext(), R.drawable.pop_out_message)
            )
        }

        lifecycleScope.launch {
            delay(3000)
            successDialog.dismiss()
        }
    }

    private fun showSuccessDialog(message: String) {
        successDialog.dismiss()
        successDialog = Dialog(requireContext()).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.loading_effect_layout)
            setCancelable(false)

            progressBar = findViewById(R.id.progressBar)
            doneLogo = findViewById(R.id.done_logo)
            successTextView = findViewById(R.id.successTextView)

            progressBar.visibility = View.GONE
            doneLogo.setImageResource(R.drawable.baseline_done_all_24)
            successTextView.text = message
            doneLogo.visibility = View.VISIBLE
            successTextView.visibility = View.VISIBLE

            show()

            window?.setLayout(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            window?.setBackgroundDrawable(
                ContextCompat.getDrawable(requireContext(), R.drawable.pop_out_message)
            )
        }

        lifecycleScope.launch {
            delay(3000)
            successDialog.dismiss()
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        doneLogo.visibility = View.GONE
        successTextView.visibility = View.GONE
        successDialog.show()
    }

    private fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}