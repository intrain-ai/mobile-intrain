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

private lateinit var sharedPrefHelper: SharedPrefHelper

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

    private fun uploadPdfWithUserId() {
        selectedPdfUri?.let { uri ->
            // Get InputStream for the content URI
            val inputStream = contentResolver.openInputStream(uri)

            // Check if the InputStream is not null before proceeding
            inputStream?.let {
                // Create a temporary file to store the uploaded PDF
                val tempFile = File.createTempFile("temp_pdf", ".pdf", cacheDir)

                // Copy data from the InputStream to the temporary file
                val outputStream = tempFile.outputStream()
                inputStream.copyTo(outputStream)

                // Use the temp file for the upload
                val pdfRequestBody = tempFile.asRequestBody("application/pdf".toMediaTypeOrNull())
                val pdfPart = MultipartBody.Part.createFormData("file", tempFile.name, pdfRequestBody)

                // Get the user ID from shared preferences
                val userId = sharedPrefHelper.getUid().toString()
                val userIdRequestBody = userId.toRequestBody("text/plain".toMediaTypeOrNull())

                // Perform the upload via Retrofit
                lifecycleScope.launch {
                    try {
                        val response = ApiConfig.api.uploadPdfWithText(pdfPart, userIdRequestBody)
                        if (response.isSuccessful) {
                            response.body()?.let { cvResponse ->
                                cvResponse.review?.let { review ->
                                    val result = "Review: ${review.overallFeedback ?: "No feedback available"}"
                                    showResult(result)
                                } ?: run {
                                    showResult("No review available.")
                                }
                            }
                        } else {
                            showResult("Failed to upload the file. Please try again.")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        showResult("Error uploading file: ${e.message}")
                    } finally {
                        showLoading(false)
                    }
                }
            } ?: run {
                // Handle the case where InputStream is null
                showResult("Error accessing the file. Please try again.")
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
