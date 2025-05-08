package com.mercu.intrain.ui.cvcheck

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
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
import com.google.ai.client.generativeai.GenerativeModel
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.mercu.intrain.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import androidx.core.graphics.createBitmap

class ReviewActivity : AppCompatActivity() {
    private var selectedPdfUri: Uri? = null

    private val pdfPickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedPdfUri = uri
                showPdfPreview(uri)
                showResult("PDF selected: ${uri.lastPathSegment}")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)
        setupButtons()
        BatasLayar()
    }

    private fun setupButtons() {
        findViewById<View>(R.id.upload_pdf_button).setOnClickListener {
            openPdfPicker()
        }

        findViewById<View>(R.id.review_button).setOnClickListener {
            selectedPdfUri?.let {
                showLoading(true)
                simulateAnalysis()
            } ?: run {
                Toast.makeText(this, "Please select a PDF first", Toast.LENGTH_SHORT).show()
            }
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
                    val bitmap = createBitmap(page.width, page.height)
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

    private fun simulateAnalysis() {
        // Simulasi proses analisis dengan delay
        findViewById<View>(R.id.review_button).postDelayed({
            showLoading(false)
            showDummyAnalysisResult()
        }, 2000)
    }

    private fun showDummyAnalysisResult() {
        val dummyResult = """
            [Dummy Analysis Result]
            
            ✔️ Good:
            - Clear work experience section
            - Proper education timeline
            - Good use of action verbs
            
            ❌ Needs Improvement:
            - Missing contact information
            - No skills section
            - Too long (3 pages)
            
            💡 Suggestions:
            - Add LinkedIn/profile link
            - Include technical skills section
            - Keep to 2 pages maximum
        """.trimIndent()

        showResult(dummyResult)
    }

    private fun showLoading(isLoading: Boolean) {
        findViewById<CircularProgressIndicator>(R.id.progressBar).visibility = if (isLoading) View.VISIBLE else View.GONE
        findViewById<View>(R.id.upload_pdf_button).isEnabled = !isLoading
        findViewById<View>(R.id.review_button).isEnabled = !isLoading
    }

    private fun showResult(result: String) {
        findViewById<MaterialCardView>(R.id.resultCard).visibility = View.VISIBLE
        findViewById<TextView>(R.id.resultTextView).text = result
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