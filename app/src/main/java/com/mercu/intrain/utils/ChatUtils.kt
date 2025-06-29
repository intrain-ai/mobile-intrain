package com.mercu.intrain.utils

import android.content.Context
import android.content.Intent
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.mercu.intrain.sharedpref.SharedPrefHelper
import com.mercu.intrain.ui.chat.DiffSelectActivity

object ChatUtils {
    
    /**
     * Checks if user has job type set in SharedPreferences
     * If not, shows dialog to input job type
     * If yes, navigates to chat
     */
    fun checkJobTypeAndOpenChat(fragment: Fragment) {
        val sharedPrefHelper = SharedPrefHelper(fragment.requireContext())
        
        if (sharedPrefHelper.hasJobType()) {
            // Job type exists, proceed to chat
            fragment.startActivity(Intent(fragment.requireContext(), DiffSelectActivity::class.java))
        } else {
            // Show alert dialog to input job type
            showJobTypeDialog(fragment, sharedPrefHelper)
        }
    }
    
    /**
     * Shows dialog to input job type and saves it to SharedPreferences
     */
    private fun showJobTypeDialog(fragment: Fragment, sharedPrefHelper: SharedPrefHelper) {
        val editText = EditText(fragment.requireContext()).apply {
            hint = "Masukkan tipe pekerjaan Anda (contoh: Software Engineer, UI/UX Designer, dll)"
            setPadding(50, 30, 50, 30)
        }

        AlertDialog.Builder(fragment.requireContext())
            .setTitle("Tipe Pekerjaan")
            .setMessage("Sebelum memulai chat, mohon masukkan tipe pekerjaan Anda")
            .setView(editText)
            .setPositiveButton("Simpan") { _, _ ->
                val jobType = editText.text.toString().trim()
                if (jobType.isNotEmpty()) {
                    sharedPrefHelper.saveJobType(jobType)
                    fragment.startActivity(Intent(fragment.requireContext(), DiffSelectActivity::class.java))
                }
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }
    
    /**
     * Checks if user has job type set in SharedPreferences
     * Returns true if job type exists, false otherwise
     */
    fun hasJobType(context: Context): Boolean {
        val sharedPrefHelper = SharedPrefHelper(context)
        return sharedPrefHelper.hasJobType()
    }
    
    /**
     * Gets the current job type from SharedPreferences
     * Returns the job type string or null if not set
     */
    fun getJobType(context: Context): String? {
        val sharedPrefHelper = SharedPrefHelper(context)
        return sharedPrefHelper.getJobType()
    }
    
    /**
     * Updates the job type in SharedPreferences
     */
    fun updateJobType(context: Context, jobType: String) {
        val sharedPrefHelper = SharedPrefHelper(context)
        sharedPrefHelper.saveJobType(jobType)
    }
    
    /**
     * Clears the job type from SharedPreferences
     */
    fun clearJobType(context: Context) {
        val sharedPrefHelper = SharedPrefHelper(context)
        sharedPrefHelper.saveJobType("")
    }
    
    /**
     * Shows a dialog to edit the current job type
     */
    fun showEditJobTypeDialog(fragment: Fragment, onJobTypeUpdated: (() -> Unit)? = null) {
        val sharedPrefHelper = SharedPrefHelper(fragment.requireContext())
        val currentJobType = sharedPrefHelper.getJobType() ?: ""
        
        val editText = EditText(fragment.requireContext()).apply {
            setText(currentJobType)
            hint = "Masukkan tipe pekerjaan Anda"
            setPadding(50, 30, 50, 30)
        }

        AlertDialog.Builder(fragment.requireContext())
            .setTitle("Edit Tipe Pekerjaan")
            .setMessage("Ubah tipe pekerjaan Anda")
            .setView(editText)
            .setPositiveButton("Simpan") { _, _ ->
                val jobType = editText.text.toString().trim()
                if (jobType.isNotEmpty()) {
                    sharedPrefHelper.saveJobType(jobType)
                    onJobTypeUpdated?.invoke()
                }
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
} 