package com.mercu.intrain.ui.profile

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.mercu.intrain.API.ApiConfig
import com.mercu.intrain.API.UpdateUserRequest
import com.mercu.intrain.R
import com.mercu.intrain.databinding.FragmentProfileBinding
import com.mercu.intrain.model.WorkExperience
import com.mercu.intrain.sharedpref.SharedPrefHelper
import com.mercu.intrain.ui.auth.LoginActivity
import com.mercu.intrain.ui.jobs.JobsActiviy
import com.mercu.intrain.ui.roadmap.RoadmapComposeActivity
import com.mercu.intrain.utils.ChatUtils
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import kotlin.jvm.java

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPrefHelper: SharedPrefHelper
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var workExperienceAdapter: WorkExperienceAdapter

    private val PROFILE_IMAGE_NAME = "profile_image.png"
    private var isEditing = false

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, it)
            saveProfileImage(bitmap)
            binding.profileImageView.setImageBitmap(bitmap)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        sharedPrefHelper = SharedPrefHelper(requireContext())
        
        val apiService = ApiConfig.api
        val factory = ProfileViewModelFactory(apiService)
        profileViewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]

        // Load profile data from SharedPref and update ViewModel
        val username = sharedPrefHelper.getUsername() ?: ""
        val email = sharedPrefHelper.getEmail() ?: ""
        val name = sharedPrefHelper.getName() ?: ""
        val jobId = sharedPrefHelper.getJobType() ?: ""
        var jobTitle = "Belum dipilih"
        // Optionally, you can map jobId to jobTitle if you store the title in SharedPreferences
        if (jobId.isNotEmpty()) {
            jobTitle = jobId // If you store the title, use it here
        }

        profileViewModel.updateProfileInfo(name, username, email)

        // Observe LiveData from ViewModel
        profileViewModel.name.observe(viewLifecycleOwner) {
            binding.nameText.text = it
        }
        profileViewModel.username.observe(viewLifecycleOwner) {
            binding.usernameText.text = "@$it"
        }
        profileViewModel.email.observe(viewLifecycleOwner) {
            binding.emailText.text = it
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadProfileImage()

        binding.profileImageView.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
        binding.cameraIcon.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        toggleEditMode(false) // Default mode: view only

        // Edit Profile Button toggle edit mode
        binding.editProfileButton.setOnClickListener {
            toggleEditMode(true)
            // Prefill edit fields
            binding.editUsername.setText(binding.usernameText.text)
            binding.editEmail.setText(binding.emailText.text)
            binding.editName.setText(sharedPrefHelper.getName() ?: "")
            binding.editPassword.setText("") // empty for security
        }

        // Cancel Edit Button
        binding.cancelEditButton.setOnClickListener {
            toggleEditMode(false)
        }

        // Save Edit Button
        binding.saveEditButton.setOnClickListener {
            val userId = sharedPrefHelper.getUid()
            if (userId.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "User ID not found", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val username = binding.editUsername.text.toString().trim()
            val name = binding.editName.text.toString().trim()
            val email = binding.editEmail.text.toString().trim()

            if (username.isEmpty() || name.isEmpty() || email.isEmpty()) {
                Toast.makeText(requireContext(), "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.saveEditButton.isEnabled = false
            binding.saveEditButton.text = "Saving..."

            lifecycleScope.launch {
                try {
                    val req = UpdateUserRequest(
                        user_id = userId,
                        username = username,
                        password = sharedPrefHelper.getPassword() ?: "",
                        name = name,
                        email = email
                    )
                    val response = ApiConfig.api.updateUser(req)

                    if (response.isSuccessful && response.body()?.message != null) {
                        sharedPrefHelper.saveUsername(username)
                        sharedPrefHelper.saveEmail(email)
                        sharedPrefHelper.saveName(name)
                        
                        profileViewModel.updateProfileInfo(name, username, email)
                        
                        Toast.makeText(requireContext(), "Profile updated!", Toast.LENGTH_SHORT).show()
                        toggleEditMode(false)
                    } else {
                        Toast.makeText(requireContext(), "Gagal: ${response.body()?.message ?: response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                } finally {
                    binding.saveEditButton.isEnabled = true
                    binding.saveEditButton.text = "Save"
                }
            }
        }


        // Change Password button placeholder
        binding.changePasswordButton.setOnClickListener {
            binding.passwordButtonLayout.visibility = View.VISIBLE
            binding.editPassword.visibility = View.VISIBLE
            binding.savePasswordButton.visibility = View.VISIBLE
            binding.cancelPasswordButton.visibility = View.VISIBLE
            binding.editPassword.requestFocus()
            Toast.makeText(requireContext(), "Masukkan password baru", Toast.LENGTH_SHORT).show()
        }

        // Save Password button
        binding.savePasswordButton.setOnClickListener {
            val newPassword = binding.editPassword.text.toString()
            if (newPassword.isEmpty()) {
                Toast.makeText(requireContext(), "Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userId = sharedPrefHelper.getUid() ?: return@setOnClickListener

            val req = UpdateUserRequest(
                user_id = userId,
                username = sharedPrefHelper.getUsername() ?: "",
                password = newPassword,
                name = sharedPrefHelper.getName() ?: "",
                email = sharedPrefHelper.getEmail() ?: ""
            )

            lifecycleScope.launch {
                try {
                    val response = ApiConfig.api.updateUser(req)
                    if (response.isSuccessful) {
                        sharedPrefHelper.savePassword(newPassword)
                        Toast.makeText(requireContext(), "Password berhasil diperbarui", Toast.LENGTH_SHORT).show()
                        binding.editPassword.visibility = View.GONE
                        binding.savePasswordButton.visibility = View.GONE
                        sharedPrefHelper.clear()
                        navigateToLogin()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Gagal update password", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.cancelPasswordButton.setOnClickListener {
            // Sembunyikan field password dan tombol
            binding.passwordButtonLayout.visibility = View.GONE
            binding.editPassword.visibility = View.GONE
            binding.savePasswordButton.visibility = View.GONE
            binding.cancelPasswordButton.visibility = View.GONE

            // Kosongkan password input untuk keamanan
            binding.editPassword.text?.clear()
        }

        // Job type button opens JobsActivity
        binding.jobTypeButton.setOnClickListener {
            val intent = Intent(requireContext(), JobsActiviy::class.java)
            startActivity(intent)
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        // Roadmap button
        binding.roadmapButton.setOnClickListener {
            val intent = Intent(requireContext(), RoadmapComposeActivity::class.java)
            startActivity(intent)
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        // Logout button
        binding.logoutButton.setOnClickListener {
            sharedPrefHelper.clear()
            Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
            navigateToLogin()
        }

        binding.jobTypeButton.setOnClickListener {
            val intent = Intent(requireContext(), JobsActiviy::class.java)
            startActivity(intent)
        }

        setupWorkExperience()
    }

    private fun toggleEditMode(enabled: Boolean) {
        isEditing = enabled
        binding.nameText.visibility = if (enabled) View.GONE else View.VISIBLE
        binding.usernameText.visibility = if (enabled) View.GONE else View.VISIBLE
        binding.emailText.visibility = if (enabled) View.GONE else View.VISIBLE

        binding.editUsername.visibility = if (enabled) View.VISIBLE else View.GONE
        binding.editEmail.visibility = if (enabled) View.VISIBLE else View.GONE
        binding.editName.visibility = if (enabled) View.VISIBLE else View.GONE
        binding.editButtonLayout.visibility = if (enabled) View.VISIBLE else View.GONE

        // Biarkan password tetap tersembunyi
        binding.editPassword.visibility = View.GONE

        binding.profileImageView.isEnabled = !enabled
        binding.cameraIcon.isEnabled = !enabled
    }


    private fun navigateToLogin() {
        startActivity(Intent(requireActivity(), LoginActivity::class.java))
        activity?.finish()
    }

    private fun saveProfileImage(bitmap: Bitmap) {
        try {
            val file = File(requireContext().filesDir, PROFILE_IMAGE_NAME)
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Failed to save profile image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadProfileImage() {
        val file = File(requireContext().filesDir, PROFILE_IMAGE_NAME)
        if (file.exists()) {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            binding.profileImageView.setImageBitmap(bitmap)
        } else {
            binding.profileImageView.setImageResource(R.drawable.ic_person)
        }
    }

    private fun setupWorkExperience() {
        // Setup RecyclerView
        workExperienceAdapter = WorkExperienceAdapter(
            emptyList(),
            onEditClick = { workExperience -> showWorkExperienceDialog(workExperience) },
            onDeleteClick = { workExperience -> showDeleteConfirmationDialog(workExperience) }
        )

        binding.workExperienceRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = workExperienceAdapter
        }

        // Load work experiences
        val userId = sharedPrefHelper.getUid()
        if (!userId.isNullOrEmpty()) {
            profileViewModel.loadWorkExperiences(userId)
        }

        // Observe work experiences
        profileViewModel.workExperiences.observe(viewLifecycleOwner) { experiences ->
            workExperienceAdapter.updateWorkExperiences(experiences)
        }

        // Observe loading state
        profileViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.addWorkExperienceButton.isEnabled = !isLoading
        }

        // Observe errors
        profileViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        // Add work experience button
        binding.addWorkExperienceButton.setOnClickListener {
            showWorkExperienceDialog()
        }

        // Edit work experience button
        binding.editExperienceButton.setOnClickListener {
            val isEditMode = workExperienceAdapter.getEditMode()
            workExperienceAdapter.setEditMode(!isEditMode)
            // Change the icon based on edit mode
            binding.editExperienceButton.setImageResource(
                if (!isEditMode) R.drawable.ic_check else R.drawable.ic_edit
            )
        }
    }

    private fun showWorkExperienceDialog(workExperience: WorkExperience? = null) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_work_experience, null)
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(if (workExperience == null) "Add Work Experience" else "Edit Work Experience")
            .setView(dialogView)
            .setPositiveButton("Save", null)
            .setNegativeButton("Cancel", null)
            .create()

        // Initialize views
        val jobTitleInput = dialogView.findViewById<TextInputEditText>(R.id.jobTitleInput)
        val companyNameInput = dialogView.findViewById<TextInputEditText>(R.id.companyNameInput)
        val jobDescInput = dialogView.findViewById<TextInputEditText>(R.id.jobDescInput)
        val startMonthInput = dialogView.findViewById<TextInputEditText>(R.id.startMonthInput)
        val startYearInput = dialogView.findViewById<TextInputEditText>(R.id.startYearInput)
        val endMonthInput = dialogView.findViewById<TextInputEditText>(R.id.endMonthInput)
        val endYearInput = dialogView.findViewById<TextInputEditText>(R.id.endYearInput)
        val isCurrentJobSwitch = dialogView.findViewById<SwitchMaterial>(R.id.isCurrentJobSwitch)

        // Pre-fill data if editing
        workExperience?.let {
            jobTitleInput.setText(it.jobTitle)
            companyNameInput.setText(it.companyName)
            jobDescInput.setText(it.jobDesc)
            startMonthInput.setText(it.startMonth.toString())
            startYearInput.setText(it.startYear.toString())
            endMonthInput.setText(it.endMonth.toString())
            endYearInput.setText(it.endYear.toString())
            isCurrentJobSwitch.isChecked = it.isCurrent

            // Apply enabled state based on current job status
            endMonthInput.isEnabled = !it.isCurrent
            endYearInput.isEnabled = !it.isCurrent
        }

        // Handle current job switch
        isCurrentJobSwitch.setOnCheckedChangeListener { _, isChecked ->
            endMonthInput.isEnabled = !isChecked
            endYearInput.isEnabled = !isChecked
        }

        dialog.setOnShowListener {
            // Set max height for dialog
            val window = dialog.window
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (resources.displayMetrics.heightPixels * 0.8).toInt()
            )

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val userId = sharedPrefHelper.getUid() ?: return@setOnClickListener

                // Validate inputs
                if (jobTitleInput.text.isNullOrEmpty() ||
                    companyNameInput.text.isNullOrEmpty() ||
                    jobDescInput.text.isNullOrEmpty() ||
                    startMonthInput.text.isNullOrEmpty() ||
                    startYearInput.text.isNullOrEmpty() ||
                    (!isCurrentJobSwitch.isChecked && (endMonthInput.text.isNullOrEmpty() || endYearInput.text.isNullOrEmpty()))
                ) {
                    Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val newWorkExperience = WorkExperience(
                    id = workExperience?.id,
                    userId = userId,
                    jobTitle = jobTitleInput.text.toString(),
                    companyName = companyNameInput.text.toString(),
                    jobDesc = jobDescInput.text.toString(),
                    startMonth = startMonthInput.text.toString().toInt(),
                    startYear = startYearInput.text.toString().toInt(),
                    endMonth = if (isCurrentJobSwitch.isChecked) 0 else endMonthInput.text.toString().toInt(),
                    endYear = if (isCurrentJobSwitch.isChecked) 0 else endYearInput.text.toString().toInt(),
                    isCurrent = isCurrentJobSwitch.isChecked
                )

                if (workExperience == null) {
                    profileViewModel.createWorkExperience(userId, newWorkExperience)
                } else {
                    profileViewModel.updateWorkExperience(userId, workExperience.id!!, newWorkExperience)
                }

                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun showDeleteConfirmationDialog(workExperience: WorkExperience) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Work Experience")
            .setMessage("Are you sure you want to delete this work experience?")
            .setPositiveButton("Delete") { _, _ ->
                val userId = sharedPrefHelper.getUid()
                if (!userId.isNullOrEmpty() && workExperience.id != null) {
                    profileViewModel.deleteWorkExperience(userId, workExperience.id)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
