package com.example.kessekolah.ui.core.beranda.forum.detailQuestion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kessekolah.R
import com.example.kessekolah.data.database.Answer
import com.example.kessekolah.data.database.PostConsultation
import com.example.kessekolah.data.remote.LoginData
import com.example.kessekolah.databinding.FragmentDetailQuestionBinding
import com.example.kessekolah.ui.adapter.AnswerListAdapter
import com.example.kessekolah.utils.LoginPreference
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class DetailQuestionFragment : Fragment() {

    private var _binding: FragmentDetailQuestionBinding? = null
    private val binding get() = _binding!!
    private lateinit var data: PostConsultation
    private lateinit var dataLogin: LoginData
    private lateinit var auth: FirebaseAuth
    private val args: DetailQuestionFragmentArgs by navArgs()

    private val viewModel: DetailQuestionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailQuestionBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        data = args.data
        auth = FirebaseAuth.getInstance()
        dataLogin = LoginPreference(requireContext()).getData()
        setupData()
        itemList()
        viewModel.fetchAnswerList(data.guestId)
    }

    private fun setupData() = with(binding) {
        tvName.text = data.name
        tvTime.text = data.timestamp
        tvQuestions.text = data.question

        if (data.description.isEmpty()) {
            tvDesc.visibility = View.GONE
        } else {
            tvDesc.text = data.description
        }

        if (data.imgURL.isEmpty()) {
            ivQuestion.visibility = View.GONE
        } else {
            Picasso.get().load(data.imgURL).into(ivQuestion)
        }

        if (dataLogin.profilePicture?.isNotEmpty() == true) {
            Picasso.get().load(dataLogin.profilePicture).into(ivUserAnswer)
        } else {
            ivUserAnswer.setImageResource(R.drawable.logo_app)
        }

        if(dataLogin.role == "Guru"){
            linearlayout.visibility = View.VISIBLE
        } else {
            linearlayout.visibility = View.GONE
        }

        btnSubmitAnswer.setOnClickListener {
            val answerText = etAnswer.text.toString().trim()
            val name = dataLogin.name ?: "Guru"
            val uid = UUID.randomUUID().toString()
            val currentDateTime = getCurrentDateTime()
            val answer = Answer(
                answerId = uid,
                name = name,
                answerText = answerText,
                timestamp = currentDateTime
            )

            viewModel.addAnswer(data.guestId, uid, answer)
            etAnswer.text.clear()
        }
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                btnSubmitAnswer.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
            } else {
                btnSubmitAnswer.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
            }
        }

        viewModel.messageText.observe(viewLifecycleOwner) { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private fun itemList() {
        val adapter = AnswerListAdapter(dataLogin.role)
        binding.rvAnswer.adapter = adapter
        binding.rvAnswer.layoutManager = LinearLayoutManager(requireContext())

        adapter.setOnItemClickCallback(object : AnswerListAdapter.OnItemClickCallback {

            override fun onDeleteClicked(data: Answer) {
                showDialog(data)
            }

        })

        viewModel.answerList.observe(viewLifecycleOwner) { questionList ->
            viewDataEmpty(questionList.isEmpty())
            questionList?.let {
                adapter.submitList(it)
            }
        }
    }

    private fun viewDataEmpty(isEmpty: Boolean) {
        with(binding) {
            if (isEmpty) imgDataEmpty.visibility = View.VISIBLE  else imgDataEmpty.visibility = View.GONE
        }
    }

    private fun showDialog(dataAnswer: Answer) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.title_dialog_question_delete))
            .setMessage("answers from \"${data.name}\" will be deleted from the database")
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.accept)) { _, _ ->
                viewModel.deleteAnswer(data.guestId, dataAnswer.answerId)
            }
            .show()
    }
}