package com.example.notesapp.activities.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
                        resourceLD.postValue(Resource.OnSuccess())
                    } else {
                        resourceLD.postValue(Resource.OnError(it.exception?.message))
                    }
                }
        }

    }
}