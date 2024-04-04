package com.example.notesapp.activities.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesapp.activities.ui.main.models.User
import com.example.notesapp.sharedPreferences
import com.example.notesapp.user
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel : ViewModel() {
    val resourceLD : MutableLiveData<Resource> = MutableLiveData()

    fun loginClicked(email: String?, password: String?) {
        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            resourceLD.postValue(Resource.OnError("Some fields are missing"))
        } else {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        sharedPreferences.user = User(
                            id = it.result?.user?.uid ?: "",
                            name = it.result?.user?.displayName ?: "",
                            email = it.result?.user?.email ?: "",
                            imageUrl = it.result?.user?.photoUrl.toString()
                        )
                        resourceLD.postValue(Resource.OnSuccess())
                    } else {
                        resourceLD.postValue(Resource.OnError(it.exception?.message))
                    }
                }
        }
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
        sharedPreferences.user = null
        resourceLD.postValue(Resource.OnSuccess())
    }
}