package com.example.notesapp

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.notesapp.databinding.FragmentAllNotesBinding

class AllNotesFragmentFragment : Fragment() {

    companion object {
        fun newInstance() = AllNotesFragmentFragment()
    }

    private val viewModel: AllNotesViewModel by viewModels()
    private lateinit var binding: FragmentAllNotesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllNotesBinding.inflate(inflater)
        return inflater.inflate(R.layout.fragment_all_notes, container, false)
    }
}