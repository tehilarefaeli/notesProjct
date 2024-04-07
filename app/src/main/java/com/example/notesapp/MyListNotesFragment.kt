package com.example.notesapp

import android.graphics.BitmapFactory
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.activities.ui.main.models.Note
import com.example.notesapp.databinding.FragmentListNotesBinding

class MyListNotesFragment : Fragment() {
    private lateinit var binding : FragmentListNotesBinding
    private lateinit var notesAdapter: NotesAdapter
    private lateinit var recyclerView: RecyclerView

    companion object {
        fun newInstance() = MyListNotesFragment()
    }

    private val viewModel: AddNoteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListNotesBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.notes_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        binding.addNote.setOnClickListener {
            AddNoteFragment.noteToEdit = null
            findNavController().navigate(R.id.action_navigation_my_notes_to_addNoteFragment)
        }

        viewModel.resourceLD.observe(viewLifecycleOwner) { resource ->
            when(resource) {
                is AddNoteViewModel.NoteResource.OnAdded -> {

                }
                is AddNoteViewModel.NoteResource.OnError -> {

                }
                is AddNoteViewModel.NoteResource.OnListUpdate -> {
                    setNotesAdapter()
                }
                is AddNoteViewModel.NoteResource.OnUpdate -> {
                    notesAdapter.notifyDataSetChanged()
                }
            }
        }
        viewModel.listenToNotesChanges()
    }

    private fun setNotesAdapter() {
        notesAdapter = NotesAdapter(viewModel.notesList, object : NoteActionListener {
            override fun onEditClicked(noteId: String) {
                AddNoteFragment.noteToEdit = viewModel.notesList.find { it.id == noteId }
                // findNavController().navigate(R.id.action_listNotesFragment_to_addNoteFragment)
            }

            override fun onDeleteClicked(noteId: String) {
                viewModel.deleteNote(noteId)
            }
        })
        recyclerView.adapter = notesAdapter
    }

    override fun onResume() {
        super.onResume()

        setNotesAdapter()
    }

    interface NoteActionListener {
        fun onEditClicked(noteId: String)
        fun onDeleteClicked(noteId: String)

    }
    // NoteViewHolder.kt
    class NoteViewHolder(view: View, private val listener: NoteActionListener): RecyclerView.ViewHolder(view) {
        private val titleTextView: TextView = view.findViewById(R.id.note_title)
        private val contentTextView: TextView = view.findViewById(R.id.note_content)
        private val ivDelete: ImageView = view.findViewById(R.id.iv_delete)
        private val ivEdit: ImageView = view.findViewById(R.id.iv_edit)
        private val ivImg: ImageView = view.findViewById(R.id.img_content)
        // Populate other views if needed

        init {
            ivEdit.setOnClickListener {
                val noteId = it.tag?.toString()  ?: return@setOnClickListener
                listener.onEditClicked(noteId)
            }

            ivDelete.setOnClickListener {
                val noteId = it.tag?.toString() ?: return@setOnClickListener
                listener.onDeleteClicked(noteId)
            }
        }

        fun bind(note: Note) {
            titleTextView.text = note.title
            contentTextView.text = note.content
//            val bitmap = note.img?.let {
//                val bytes = it.toByteArray()
//                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
//            }

            //ivImg.setImageBitmap(bitmap)
            // ivImg.tag =note.img
            ivEdit.tag = note.id
            ivDelete.tag = note.id
        }
    }

    // NotesAdapter.kt
    class NotesAdapter(private val notes: List<Note>, private val listener: NoteActionListener): RecyclerView.Adapter<NoteViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.note_list_item, parent, false)
            return NoteViewHolder(view,listener)
        }

        override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
            holder.bind(notes[position])
        }

        override fun getItemCount() = notes.size
    }
}