package com.mercu.intrain.ui.profile

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
import com.mercu.intrain.R
import com.mercu.intrain.databinding.FragmentProfileBinding
import com.mercu.intrain.sharedpref.SharedPrefHelper
import com.mercu.intrain.ui.LoginActivity
import java.io.File
import java.io.FileOutputStream

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPrefHelper: SharedPrefHelper
    private lateinit var profileViewModel: ProfileViewModel
    private val PROFILE_IMAGE_NAME = "profile_image.png"

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
        sharedPrefHelper = SharedPrefHelper(requireContext())
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Load profile data
        sharedPrefHelper.getUsername()?.let { username ->
            sharedPrefHelper.getEmail()?.let { email ->
                profileViewModel.updateProfileInfo(username, email)
            }
        }

        // Observe profile data
        profileViewModel.username.observe(viewLifecycleOwner) { username ->
            binding.usernameText.text = username
        }

        profileViewModel.email.observe(viewLifecycleOwner) { email ->
            binding.emailText.text = email
        }

        binding.changePasswordButton.setOnClickListener {
            // Handle password change logic
            Toast.makeText(requireContext(), "Password changed successfully", Toast.LENGTH_SHORT).show()
        }

        binding.logoutButton.setOnClickListener {
            sharedPrefHelper.clear()
            Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
            navigateToLogin()
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadProfileImage()
        binding.profileImageView.setOnClickListener { pickImageLauncher.launch("image/*") }
        binding.cameraIcon.setOnClickListener { pickImageLauncher.launch("image/*") }
    }

    private fun navigateToLogin() {
        val intent = Intent(activity, LoginActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun saveProfileImage(bitmap: Bitmap) {
        val file = File(requireContext().filesDir, PROFILE_IMAGE_NAME)
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}