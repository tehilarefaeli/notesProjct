package com.example.notesapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.notesapp.activities.ui.main.Resource
import com.example.notesapp.activities.ui.main.models.Note
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class AddNoteViewModel : ViewModel() {
    var notesList = mutableListOf<Note>()
    val resourceLD: MutableLiveData<NoteResource> = MutableLiveData()
    val DEFAULT_USER_ID = "default_user_id"

    fun sharePost(title: String, content: String) {
        var errorMsg = ""
        if (title.isEmpty()) {
            errorMsg+="Title field is missing.\n"
        }

        if (content.isEmpty()) {
            errorMsg+="Content field is missing.\n"
        }

        if (errorMsg.isNotEmpty()){
            resourceLD.postValue(NoteResource.OnError(errorMsg))
            return
        }

        addNewNote(title, content)
    }

    private fun addNewNote(title: String, content: String) {
        val note = Note(
            title = title,
            content = content,
            author = sharedPreferences.user?.id ?: DEFAULT_USER_ID
        )

        // Save the note to Firestore
        val db = FirebaseFirestore.getInstance()
        db.collection("notes") // Specify your collection name
            .add(note)
            .addOnSuccessListener { documentReference ->
                // Note added successfully
                resourceLD.postValue(NoteResource.OnAdded())
            }
            .addOnFailureListener { e ->
                // Failed to add note
                resourceLD.postValue(NoteResource.OnError("Failed to add note: ${e.message}"))
            }
    }

    fun listenToNotesChanges() {
        var userId = sharedPreferences.user?.id ?: DEFAULT_USER_ID
        val db = FirebaseFirestore.getInstance()
        db.collection("notes")
            .whereEqualTo("author", userId) // Filter by userId or author
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    resourceLD.postValue(NoteResource.OnError("Listen failed: ${e.message}"))
                    return@addSnapshotListener
                }

                notesList = mutableListOf<Note>()
                for (doc in snapshots!!) {
                    val note = doc.toObject(Note::class.java).copy(id = doc.id)
                    notesList.add(note)
                }
                resourceLD.postValue(NoteResource.OnListUpdate(notesList))
            }
    }

    sealed class NoteResource {
        class OnAdded : NoteResource()
        data class OnError(val error: String) : NoteResource()
        data class OnListUpdate(val notes: List<Note>) : NoteResource() // Assuming you have a Note data class
    }

}