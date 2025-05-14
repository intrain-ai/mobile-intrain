package com.mercu.intrain.ui.cvcheck

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mercu.intrain.API.ApiConfig
import com.mercu.intrain.API.ApiService
import com.google.ai.client.generativeai.GenerativeModel
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.mercu.intrain.API.CvResponse
import com.mercu.intrain.R
import com.mercu.intrain.sharedpref.SharedPrefHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import com.mercu.intrain.API.SectionsItem

private lateinit var sharedPrefHelper: SharedPrefHelper

class ReviewActivity : AppCompatActivity() {
    private var selectedPdfUri: Uri? = null
    private lateinit var sectionsAdapter: SectionsAdapter

    private val pdfPickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedPdfUri = uri
                showPdfPreview(uri)
                Toast.makeText(this, "PDF selected: ${uri.lastPathSegment}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)
        setupButtons()
        setupRecyclerView()
        sharedPrefHelper = SharedPrefHelper(this)
        BatasLayar()
    }

    private fun setupButtons() {
        findViewById<View>(R.id.upload_pdf_button).setOnClickListener {
            openPdfPicker()
        }

        findViewById<View>(R.id.review_button).setOnClickListener {
            selectedPdfUri?.let {
                showLoading(true)
                uploadPdfWithUserId()
            } ?: run {
                Toast.makeText(this, "Please select a PDF first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.sectionsRecyclerView)
        sectionsAdapter = SectionsAdapter()
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ReviewActivity)
            adapter = sectionsAdapter
        }
    }

    private fun openPdfPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
        }
        pdfPickerLauncher.launch(intent)
    }

    private fun showPdfPreview(uri: Uri) {
        val pdfPreview: ImageView = findViewById(R.id.pdfPreview)
        try {
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val fileDescriptor: ParcelFileDescriptor? = contentResolver.openFileDescriptor(uri, "r")

            fileDescriptor?.use {
                val renderer = PdfRenderer(it)
                if (renderer.pageCount > 0) {
                    val page = renderer.openPage(0)
                    val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    pdfPreview.setImageBitmap(bitmap)
                    page.close()
                }
                renderer.close()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error loading PDF preview", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showResult(response: CvResponse) {
        findViewById<MaterialCardView>(R.id.resultCard).visibility = View.VISIBLE
        
        // Set overall feedback
        findViewById<TextView>(R.id.overallFeedbackTextView).text = response.review?.overallFeedback

        // Set ATS status
        val atsStatusCard = findViewById<MaterialCardView>(R.id.atsStatusCard)
        val atsStatusIcon = findViewById<ImageView>(R.id.atsStatusIcon)
        val atsStatusText = findViewById<TextView>(R.id.atsStatusText)

        if (response.review?.atsPassed == true) {
            atsStatusIcon.setImageResource(R.drawable.ic_check_circle)
            atsStatusIcon.setColorFilter(ContextCompat.getColor(this, R.color.success))
            atsStatusText.text = "ATS Friendly"
        } else {
            atsStatusIcon.setImageResource(R.drawable.ic_warning)
            atsStatusIcon.setColorFilter(ContextCompat.getColor(this, R.color.warning))
            atsStatusText.text = "Needs ATS Optimization"
        }

        // Set section reviews
        response.sections?.let { sections ->
            sectionsAdapter.submitList(sections.filterNotNull())
        }
    }

    private fun uploadPdfWithUserId() {
        selectedPdfUri?.let { uri ->
            val inputStream = contentResolver.openInputStream(uri)
            inputStream?.let {
                val tempFile = File.createTempFile("temp_pdf", ".pdf", cacheDir)
                val outputStream = tempFile.outputStream()
                inputStream.copyTo(outputStream)

                val pdfRequestBody = tempFile.asRequestBody("application/pdf".toMediaTypeOrNull())
                val pdfPart = MultipartBody.Part.createFormData("file", tempFile.name, pdfRequestBody)
                val userId = sharedPrefHelper.getUid().toString()
                val userIdRequestBody = userId.toRequestBody("text/plain".toMediaTypeOrNull())

                lifecycleScope.launch {
                    try {
                        val response = ApiConfig.api.uploadPdfWithText(pdfPart, userIdRequestBody)
                        if (response.isSuccessful) {
                            response.body()?.let { cvResponse ->
                                showResult(cvResponse)
                            }
                        } else {
                            Toast.makeText(this@ReviewActivity, "Failed to upload the file. Please try again.", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(this@ReviewActivity, "Error uploading file: ${e.message}", Toast.LENGTH_SHORT).show()
                    } finally {
                        showLoading(false)
                    }
                }
            } ?: run {
                Toast.makeText(this, "Error accessing the file. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getRealPathFromURI(uri: Uri): String? {
        if ("content" == uri.scheme) {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                val columnIndex = it.getColumnIndex("_data")
                if (columnIndex != -1) {
                    it.moveToFirst()
                    return it.getString(columnIndex)
                }
            }
        }
        return null
    }

    private fun showLoading(isLoading: Boolean) {
        findViewById<CircularProgressIndicator>(R.id.progressBar).visibility = if (isLoading) View.VISIBLE else View.GONE
        findViewById<View>(R.id.upload_pdf_button).isEnabled = !isLoading
        findViewById<View>(R.id.review_button).isEnabled = !isLoading
    }

    private fun BatasLayar() {
        val rootView = findViewById<ViewGroup>(android.R.id.content).getChildAt(0)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(insets.left, insets.top, insets.right, insets.bottom)
            windowInsets
        }
    }
}

class SectionsAdapter : RecyclerView.Adapter<SectionsAdapter.SectionViewHolder>() {
    private var sections: List<SectionsItem> = emptyList()

    fun submitList(newSections: List<SectionsItem>) {
        sections = newSections
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_section_review, parent, false)
        return SectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        holder.bind(sections[position])
    }

    override fun getItemCount() = sections.size

    class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sectionTitle: TextView = itemView.findViewById(R.id.sectionTitleTextView)
        private val sectionFeedback: TextView = itemView.findViewById(R.id.sectionFeedbackTextView)
        private val improvementIcon: ImageView = itemView.findViewById(R.id.improvementIcon)

        fun bind(section: SectionsItem) {
            sectionTitle.text = section.section?.replace("_", " ")?.replaceFirstChar { it.uppercase() } ?: "-"
            sectionFeedback.text = section.feedback ?: ""
            if (section.needsImprovement == true) {
                improvementIcon.setImageResource(R.drawable.ic_warning)
                improvementIcon.setColorFilter(ContextCompat.getColor(itemView.context, R.color.warning))
            } else {
                improvementIcon.setImageResource(R.drawable.ic_check_circle)
                improvementIcon.setColorFilter(ContextCompat.getColor(itemView.context, R.color.success))
            }
        }
    }
}
