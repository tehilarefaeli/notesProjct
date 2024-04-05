package com.example.notesapp.activities.ui.main

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.location.Address
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Video.Media
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.notesapp.R
import com.example.notesapp.databinding.FragmentCreateProfileBinding

class CreateProfileFragment : Fragment() {



    private val viewModel: CreateProfileViewModel by viewModels()

    private lateinit var binding : FragmentCreateProfileBinding
    private var bitmap :Bitmap ?= null


    private val cameraLauncher :ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){result ->
        if (result.resultCode == Activity.RESULT_OK){
            bitmap = result.data?.extras?.get("data") as? Bitmap
            binding.profileImage.setImageBitmap(bitmap)
        }
    }

    private val galleryLauncher :ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){result ->
        if (result.resultCode == Activity.RESULT_OK){
            bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, result.data?.data)
            binding.profileImage.setImageBitmap(bitmap)
        }
    }

    private val locationLauncher :ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){result ->
        if (result.resultCode == Activity.RESULT_OK){
            binding.location.setText((result.data?.extras?.getParcelable("address") as? Address)?.getAddressLine(0))
            binding.profileImage.setImageBitmap(bitmap)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateProfileBinding.inflate(inflater)
        binding.camera.setOnClickListener {
            //openCamera()
        }
        binding.gallery.setOnClickListener {
            openGallery()
        }
        binding.location.setOnClickListener{
            locationLauncher.launch(Intent(requireContext(), LocationActivity::class.java))
        }
        binding.signUp.setOnClickListener {
            viewModel.onSignUpClicked(binding.email.text.toString(), binding.password.text.toString(),
               binding.name.text.toString(),  binding.location.text.toString(), bitmap )
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.userLD.observe(viewLifecycleOwner) {
            binding.name.setText(it.name)
            binding.email.setText(it.email)
            binding.password.visibility = View.GONE
            binding.email.isEnabled = false
            binding.signUp.setText("update")
            Glide.with(requireContext()).load(it.imageUrl)
                .placeholder(R.drawable.person)
                .into(binding.profileImage)
            binding.location.setText(it.location)

        }
        viewModel.resourceLD.observe(viewLifecycleOwner){
            when(it) {
                is Resource.OnSuccess -> {
                    Toast.makeText(requireContext(), getString(R.string.create_profile_success), Toast.LENGTH_LONG).show()
                    findNavController().popBackStack()
                }
                is Resource.OnError -> {
                    AlertDialog.Builder(requireContext())
                        .setTitle(R.string.error)
                        .setMessage(it.error)
                        .show()
                }
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.type = "image/*"
        galleryLauncher.launch(intent)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(intent)
    }


}