import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mercu.intrain.API.ApiConfig
import com.mercu.intrain.API.CvResponse
import com.mercu.intrain.sharedpref.SharedPrefHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class ReviewViewModel : ViewModel() {
    private val _pdfPreview = MutableStateFlow<Bitmap?>(null)
    val pdfPreview: StateFlow<Bitmap?> = _pdfPreview

    private val _cvResult = MutableStateFlow<CvResponse?>(null)
    val cvResult: StateFlow<CvResponse?> = _cvResult

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var selectedPdfUri: Uri? = null

    fun getSelectedPdfUri(): Uri? = selectedPdfUri

    fun setSelectedPdfUri(context: Context, uri: Uri) {
        selectedPdfUri = uri
        generatePdfPreview(context, uri)
    }

    private fun generatePdfPreview(context: Context, uri: Uri) {
        try {
            val fileDescriptor: ParcelFileDescriptor? =
                context.contentResolver.openFileDescriptor(uri, "r")
            fileDescriptor?.use {
                val renderer = PdfRenderer(it)
                if (renderer.pageCount > 0) {
                    val page = renderer.openPage(0)
                    val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    _pdfPreview.value = bitmap
                    page.close()
                }
                renderer.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun uploadPdf(context: Context) {
        val uri = selectedPdfUri ?: return
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                inputStream?.let {
                    val tempFile = File.createTempFile("temp_pdf", ".pdf", context.cacheDir)
                    tempFile.outputStream().use { output -> inputStream.copyTo(output) }

                    val pdfRequestBody = tempFile.asRequestBody("application/pdf".toMediaTypeOrNull())
                    val pdfPart = MultipartBody.Part.createFormData("file", tempFile.name, pdfRequestBody)

                    val sharedPrefHelper = SharedPrefHelper(context)
                    val userId = sharedPrefHelper.getUid().toString()
                    val userIdRequestBody = userId.toRequestBody("text/plain".toMediaTypeOrNull())

                    val response = ApiConfig.api.uploadPdfWithText(pdfPart, userIdRequestBody)
                    if (response.isSuccessful) {
                        _cvResult.value = response.body()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }


}
