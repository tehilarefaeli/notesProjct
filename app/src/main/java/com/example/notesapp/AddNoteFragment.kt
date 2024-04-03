package com.example.notesapp

import android.app.AlertDialog
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.notesapp.activities.ui.main.Resource
import com.example.notesapp.databinding.ActivityMainBinding
import com.example.notesapp.databinding.FragmentAddNoteBinding
import com.example.notesapp.databinding.FragmentCreateProfileBinding

class AddNoteFragment : Fragment() {
    private lateinit var binding : FragmentAddNoteBinding

    companion object {
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSharePost.setOnClickListener {
            viewModel.sharePost(
                binding.etPostTitle.text.toString()
                ,binding.etPostContent.text.toString())
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
            }
        }
    }
}