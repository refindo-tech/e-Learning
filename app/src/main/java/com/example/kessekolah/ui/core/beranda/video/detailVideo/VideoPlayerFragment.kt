package com.example.kessekolah.ui.core.beranda.video.detailVideo

import android.app.AlertDialog
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.kessekolah.R
import com.example.kessekolah.data.database.VideoList
import com.example.kessekolah.databinding.FragmentVideoPlayerBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions

class VideoPlayerFragment : Fragment() {

    private var _binding: FragmentVideoPlayerBinding? = null
    private val binding get() = _binding!!
    private lateinit var data: VideoList
    private val viewModel: VideoPlayerViewModel by viewModels()
    private val args: VideoPlayerFragmentArgs by navArgs()

    private lateinit var youTubePlayer: YouTubePlayer

    private val questions = listOf(
        "Do you feel that the content of this lesson is easy to understand?",
        "Is the presentation of this lesson interesting and visually comfortable?",
        "Are the visual elements in this app clear and helpful?",
        "Does this app support the health learning process?"
    )

    private val answers = mutableListOf<String?>()

    private var isFullScreen = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        data = args.video

        setData()
        setVideo()

        binding.fabQuiz.setOnClickListener {
            showQuizDialog()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isFullScreen) {
                    youTubePlayer.toggleFullscreen()
                } else {
                    findNavController().navigateUp()
                }
            }
        })
    }

    private fun setData() = with(binding.bannerMateri) {
        ilusBanner.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                requireContext().resources
                    .getIdentifier("ilus_banner_${data.dataIlus}", "drawable", requireContext().packageName)
            )
        )
        backColorBanner.setBackgroundColor(
            Color.parseColor(data.backColorBanner)
        )
        textNews.text = data.judul
    }

    private fun setVideo() = with(binding){
        val videoPlayer = ytplayer
        val fullScreenContainer = fullscreenContainer
        lifecycle.addObserver(videoPlayer)
        val url = data.videoUrl
        val videoId = extractVideoId(url)

        videoPlayer.addFullscreenListener(object : FullscreenListener {
            override fun onEnterFullscreen(fullscreenView: View, exitFullscreen: () -> Unit) {
                isFullScreen = true
                fullScreenContainer.visibility = View.VISIBLE
                videoPlayer.visibility = View.GONE
                appBarLayout.visibility = View.GONE
                topAppBar.visibility = View.GONE
                bannerMateri.cardDetail.visibility = View.GONE
                fabQuiz.visibility = View.GONE
                fullScreenContainer.addView(fullscreenView)
            }

            override fun onExitFullscreen() {
                isFullScreen = false
                fullScreenContainer.visibility = View.GONE
                videoPlayer.visibility = View.VISIBLE
                appBarLayout.visibility = View.VISIBLE
                topAppBar.visibility = View.VISIBLE
                bannerMateri.cardDetail.visibility = View.VISIBLE
                fabQuiz.visibility = View.VISIBLE
                fullScreenContainer.removeAllViews()
            }
        })

        val youtubePlayerListener = object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                this@VideoPlayerFragment.youTubePlayer = youTubePlayer
                youTubePlayer.loadVideo(videoId, 0f)
            }
        }

        val iFramePlayerOptions = IFramePlayerOptions.Builder()
            .controls(1)
            .fullscreen(1)
            .build()

        videoPlayer.enableAutomaticInitialization = false
        videoPlayer.initialize(youtubePlayerListener,iFramePlayerOptions)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            if (!isFullScreen){
                youTubePlayer.toggleFullscreen()
            }
        }else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            if (isFullScreen){
                youTubePlayer.toggleFullscreen()
            }
        }
    }

    private fun extractVideoId(url: String): String {
        return url.substringAfter("https://youtu.be/").substringBefore("?")
    }

    private fun showQuizDialog() {
        var currentQuestion = 0

        val dialogView = layoutInflater.inflate(R.layout.dialog_quiz, null)
        val questionText = dialogView.findViewById<TextView>(R.id.tv_quiz_question)
        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.radioGroup_quiz)
        val nextButton = dialogView.findViewById<Button>(R.id.btn_next)

        questionText.text = questions[currentQuestion]

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        nextButton.setOnClickListener {
            val selectedOptionId = radioGroup.checkedRadioButtonId
            if (selectedOptionId == -1) {
                Toast.makeText(requireContext(), "Select the answer first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedRadioButton = dialogView.findViewById<RadioButton>(selectedOptionId)
            answers.add(selectedRadioButton.text.toString())

            if (currentQuestion < questions.size - 1) {
                // Move to the next question
                currentQuestion++
                questionText.text = questions[currentQuestion]
                radioGroup.clearCheck()

                if (currentQuestion == questions.size - 1) {
                    nextButton.text = "Submit"
                }
            } else {
                // Submit the answers
                dialog.dismiss()
                Toast.makeText(requireContext(), "Quiz completed. Answers: $answers", Toast.LENGTH_LONG).show()
                findNavController().navigateUp() // Navigate up after submit
            }
        }
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}