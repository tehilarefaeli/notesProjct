package com.example.notesapp.activities.ui.main

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.notesapp.R
import com.example.notesapp.databinding.FragmentMainBinding

class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var viewModel: LoginViewModel
    var binding : FragmentMainBinding ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(layoutInflater)
        return binding?.root!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.resourceLD.observe(viewLifecycleOwner) {resource->
            when(resource) {
                is Resource.OnSuccess -> {
                    Toast.makeText(requireContext(), getString(R.string.login_successfully), Toast.LENGTH_LONG).show()
                }
                is Resource.OnError -> {
                    AlertDialog.Builder(requireContext())
                        .setTitle(R.string.error)
                        .setMessage(resource.error)
                        .show()
                }
            }
        }

        binding?.login?.setOnClickListener {
            viewModel.loginClicked(binding?.email?.text?.toString(), binding?.password?.text?.toString())
        }

        binding?.signUp?.setOnClickListener {
            findNavController().navigate(R.id.login_to_create_action)
        }
    }

}