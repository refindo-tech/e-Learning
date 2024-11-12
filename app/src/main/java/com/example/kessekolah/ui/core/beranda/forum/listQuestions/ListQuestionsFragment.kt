package com.example.kessekolah.ui.core.beranda.forum.listQuestions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kessekolah.R
import com.example.kessekolah.data.database.PostConsultation
import com.example.kessekolah.data.remote.LoginData
import com.example.kessekolah.databinding.FragmentListQuestionsBinding
import com.example.kessekolah.ui.adapter.QuestionListAdapter
import com.example.kessekolah.utils.LoginPreference
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ListQuestionsFragment : Fragment() {

    private var _binding: FragmentListQuestionsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ListQuestionsViewModel by viewModels()
    private lateinit var dataLogin: LoginData


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListQuestionsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataLogin = LoginPreference(requireContext()).getData()
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        itemListGuru()
        buttonCLick()
    }

    private fun itemListGuru() {
        val adapter = QuestionListAdapter(dataLogin.role)
        binding.rvQuestions.adapter = adapter
        binding.rvQuestions.layoutManager = LinearLayoutManager(requireContext())

        adapter.setOnItemClickCallback(object : QuestionListAdapter.OnItemClickCallback {

            override fun onDeleteClicked(data: PostConsultation) {
                showDialog(data)
            }

            override fun onItemClicked(data: PostConsultation) {
                val action = ListQuestionsFragmentDirections.actionListQuestionsFragmentToDetailQuestionFragment(data)
                findNavController().navigate(action)
            }

        })

        viewModel.questionList.observe(viewLifecycleOwner) { questionList ->
            viewDataEmpty(questionList.isEmpty())
            questionList?.let {
                adapter.submitList(it)
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) { loading ->
            if (loading) {
                binding.loadingIndicator.visibility = View.VISIBLE
                binding.rvQuestions.visibility = View.GONE
            } else {
                binding.loadingIndicator.visibility = View.GONE
                binding.rvQuestions.visibility = View.VISIBLE
            }
        }
    }


    private fun buttonCLick() {
        with(binding) {
            btnAddQuestion.setOnClickListener {
                findNavController().navigate(R.id.action_listQuestionsFragment_to_addQuestionFragment)
            }
        }
    }

    private fun viewDataEmpty(isEmpty: Boolean) {
        with(binding) {
            if (isEmpty) imgDataEmpty.visibility = View.VISIBLE  else imgDataEmpty.visibility = View.GONE
        }
    }

    private fun showDialog(data: PostConsultation) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.title_dialog_question_delete))
            .setMessage("questions from \"${data.name}\" will be deleted from the database")
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.accept)) { _, _ ->
                viewModel.deleteQuestion(data)
            }
            .show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}