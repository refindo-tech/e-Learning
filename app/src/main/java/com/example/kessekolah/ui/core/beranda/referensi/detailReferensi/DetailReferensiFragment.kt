package com.example.kessekolah.ui.core.beranda.referensi.detailReferensi

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.example.kessekolah.R
import com.example.kessekolah.data.database.MateriData
import com.example.kessekolah.data.remote.LoginData
import com.example.kessekolah.databinding.FragmentDetailReferensiBinding
import com.example.kessekolah.databinding.FragmentFlipBookTestBinding
import com.example.kessekolah.model.BookMarkViewModel
import com.example.kessekolah.model.ListMateriViewModel
import com.example.kessekolah.ui.adapter.ScreenSlideRecyclerAdapter
import com.example.kessekolah.utils.LoginPreference
import com.example.kessekolah.viewModel.ViewModelFactoryBookMark
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.wajahatkarim3.easyflipviewpager.BookFlipPageTransformer2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class DetailReferensiFragment : Fragment() {

    private var _binding: FragmentDetailReferensiBinding? = null
    private val binding get() = _binding!!

    private val args: DetailReferensiFragmentArgs by navArgs()
    private lateinit var data: MateriData

    private lateinit var dataLogin: LoginData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailReferensiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        data = args.data
        dataLogin = LoginPreference(requireContext()).getData()

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        setData()
        loadData(data)
    }

    private fun setData() = with(binding.bannerReferensi) {
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

    private fun loadData(data: MateriData) {
        when {
            data.fileName.endsWith(".pdf", true) -> {
                pdfToBitmap(data)
            }
            data.fileName.endsWith(".png", true) -> {
                loadImage(data.fileUrl)
            }
            else -> {
                showUnsupportedFileMessage()
            }
        }
    }

    private fun showUnsupportedFileMessage() {
        binding.flipViewPager.visibility = View.GONE
        binding.tvPdfError.visibility = View.VISIBLE
    }

    private fun loadImage(fileUrl: String) {
        binding.loadingProgressBar.visibility = View.VISIBLE
        binding.flipViewPager.visibility = View.GONE
        binding.imageView.visibility = View.VISIBLE

        Picasso.get()
            .load(fileUrl)
            .into(binding.imageView, object : Callback {
                override fun onSuccess() {
                    binding.loadingProgressBar.visibility = View.GONE
                }

                override fun onError(e: Exception?) {
                    binding.loadingProgressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun pdfToBitmap(data: MateriData) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                binding.loadingProgressBar.visibility = View.VISIBLE
                binding.imageView.visibility = View.GONE
                binding.flipViewPager.visibility = View.VISIBLE

                // Download PDF file in the background
                val pdfFile = withContext(Dispatchers.IO) {
                    downloadPdfFile(data.fileUrl)
                }

                // Open PDF using PdfRenderer
                val parcelFileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
                val pdfRenderer = PdfRenderer(parcelFileDescriptor)

                // List to store bitmaps of each page
                val bitmapList = mutableListOf<Bitmap>()

                // Process each page of the PDF
                for (pageIndex in 0 until pdfRenderer.pageCount) {
                    val page = pdfRenderer.openPage(pageIndex)
                    val width = page.width
                    val height = page.height

                    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    bitmapList.add(bitmap)
                    page.close()
                }

                pdfRenderer.close()

                binding.loadingProgressBar.visibility = View.GONE

                withContext(Dispatchers.Main) {
                    bookFlip(bitmapList)
                }

            } catch (ex: Exception) {
                Log.e("PDF TO BITMAP", ex.toString())
                binding.loadingProgressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Failed to load PDF", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun downloadPdfFile(pdfUrl: String): File = withContext(Dispatchers.IO) {
        val url = URL(pdfUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()

        val inputStream = connection.inputStream
        val file = File(requireContext().cacheDir, "downloaded_file.pdf")
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)

        outputStream.close()
        inputStream.close()

        file
    }

    private fun bookFlip(bitmapList: List<Bitmap>) = with(binding) {
        val sliderAdapter = ScreenSlideRecyclerAdapter(ArrayList<Bitmap>())
        sliderAdapter.setItems(bitmapList)

        flipViewPager.adapter = sliderAdapter

        val bookTransformer = BookFlipPageTransformer2()
        flipViewPager.setPageTransformer(bookTransformer)
        flipViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}