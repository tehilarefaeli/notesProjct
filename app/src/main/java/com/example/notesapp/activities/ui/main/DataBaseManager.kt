package com.example.notesapp.activities.ui.main

import com.example.notesapp.activities.ui.main.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore

object DataBaseManager {

    private const val USERS = "users"
    fun saveUser(user: User,  listener: ((Task<Void>) -> Unit)){
        FirebaseFirestore.getInstance()
            .collection(USERS)
            .document(user.id)
            .set(user)
            .addOnCompleteListener(listener)
    }
}