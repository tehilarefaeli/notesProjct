package com.example.notesapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.example.notesapp.activities.ui.main.Resource
import com.example.notesapp.activities.ui.main.models.Note
import com.example.notesapp.databinding.ActivityMainBinding
import com.example.notesapp.databinding.FragmentAddNoteBinding
import com.example.notesapp.databinding.FragmentCreateProfileBinding

class AddNoteFragment : Fragment() {
    private lateinit var binding : FragmentAddNoteBinding
    private var bitmap :Bitmap ?= null


    private val cameraLauncher : ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == Activity.RESULT_OK){
            bitmap = result.data?.extras?.get("data") as? Bitmap
            binding.profileImage.setImageBitmap(bitmap)
        }
    }

    private val galleryLauncher : ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == Activity.RESULT_OK){
            bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, result.data?.data)
            binding.profileImage.setImageBitmap(bitmap)
        }
    }

    companion object {
        var noteToEdit: Note? = null
        fun newInstance() = AddNoteFragment()
    }

    private val viewModel: AddNoteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddNoteBinding.inflate(inflater)
        val callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner){
            //findNavController().navigate(R.id.action_addNoteFragment_to_listNotesFragment)
        }
        callback.isEnabled =true
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

         noteToEdit?.let {
                binding.etPostTitle.setText(it.title)
                binding.etPostContent.setText(it.content)
            }

        binding.btnSharePost.setOnClickListener {
            viewModel.sharePost(
                binding.etPostTitle.text.toString(),
                binding.etPostContent.text.toString(),
                bitmap,
                noteToEdit?.id
            )
        }
        binding.camera.setOnClickListener {
            openCamera()
        }
        binding.gallery.setOnClickListener {
            openGallery()
        }



        viewModel.resourceLD.observe(viewLifecycleOwner) { resource ->
            when(resource) {
                is AddNoteViewModel.NoteResource.OnAdded -> {
                    Toast.makeText(requireContext(), getString(R.string.post_shared_successfully), Toast.LENGTH_LONG).show()
                    findNavController().popBackStack()
                }
                is AddNoteViewModel.NoteResource.OnError -> {
                    AlertDialog.Builder(requireContext())
                        .setTitle(R.string.error)
                        .setMessage(resource.error)
                        .show()
                }
                is AddNoteViewModel.NoteResource.OnListUpdate -> {

                }
                is AddNoteViewModel.NoteResource.OnUpdate -> {
                    noteToEdit = null
                    Toast.makeText(requireContext(), getString(R.string.post_update_successfully), Toast.LENGTH_LONG).show()
                    findNavController().popBackStack()
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