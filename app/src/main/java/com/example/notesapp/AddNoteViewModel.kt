package com.example.notesapp

import android.graphics.Bitmap
import android.media.Image
import android.provider.MediaStore.Images
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.notesapp.activities.ui.main.Resource
import com.example.notesapp.activities.ui.main.models.Note
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.Date

class AddNoteViewModel : ViewModel() {
    var notesList = mutableListOf<Note>()
    val resourceLD: MutableLiveData<NoteResource> = MutableLiveData()
    val DEFAULT_USER_ID = "default_user_id"

    fun sharePost(title: String, content: String, bitmap:Bitmap?, noteId: String? = null) {
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

        if(noteId != null) {
            editNote(noteId, title, content)
        }else {
            bitmap?.let {
                val imageRef: StorageReference = FirebaseStorage.getInstance().reference.child("users/${noteId}.jpg")
                val bos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
                val data = bos.toByteArray()
                imageRef.putBytes(data)
                    .addOnSuccessListener {
                        imageRef.downloadUrl.addOnSuccessListener {
                            addNewNote(title, content , it.toString())
                        }.addOnFailureListener {
                            resourceLD.postValue(NoteResource.OnError(errorMsg))
                        }
                    }
                    .addOnFailureListener{
                        resourceLD.postValue(NoteResource.OnError(errorMsg))
                    }
            } ?: run {
                addNewNote(title, content , "")
            }
        }
    }

    private fun addNewNote(title: String, content: String ,bitmap: String?) {


        val note = Note(
            title = title,
            content = content,
            img = bitmap,
            author = sharedPreferences.user?.id ?: DEFAULT_USER_ID
        )

        // Save the note to Firestore
        val db = FirebaseFirestore.getInstance()
        db.collection("notes") // Specify your collection name
            .add(note)
            .addOnSuccessListener { documentReference ->
                // Note added successfully
                resourceLD.postValue(NoteResource.OnAdded)
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

    fun listenToNotesChangesWithOutUser() {
     //   var userId = sharedPreferences.user?.id ?: DEFAULT_USER_ID
        val db = FirebaseFirestore.getInstance()
        db.collection("notes")
           // .whereEqualTo("author", userId) // Filter by userId or author
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

    fun editNote(noteId: String, newTitle: String, newContent: String) {
        val db = FirebaseFirestore.getInstance()
        val noteUpdateMap = mapOf(
            "title" to newTitle,
            "content" to newContent,
            "lastModified" to FieldValue.serverTimestamp() // Optionally update a lastModified field
        )

        db.collection("notes").document(noteId)
            .update(noteUpdateMap)
            .addOnSuccessListener {
                resourceLD.postValue(NoteResource.OnUpdate) // Consider creating and posting a more appropriate resource state
            }
            .addOnFailureListener { e ->
                resourceLD.postValue(NoteResource.OnError("Failed to update note: ${e.message}"))
            }
    }

    fun deleteNote(noteId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("notes").document(noteId)
            .delete()
            .addOnSuccessListener {
                resourceLD.postValue(NoteResource.OnListUpdate(notesList)) // Consider creating and posting a more appropriate resource state
            }
            .addOnFailureListener { e ->
                resourceLD.postValue(NoteResource.OnError("Failed to delete note: ${e.message}"))
            }
    }


    sealed class NoteResource {
        data object OnUpdate : NoteResource()
        data object  OnAdded : NoteResource()
        data class OnError(val error: String) : NoteResource()
        data class OnListUpdate(val notes: List<Note>) : NoteResource() // Assuming you have a Note data class
    }



}



