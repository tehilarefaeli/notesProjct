package com.example.notesapp.activities.ui.main

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.notesapp.activities.ui.main.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream

class CreateProfileViewModel : ViewModel() {

    val resourceLD: MutableLiveData<Resource> = MutableLiveData()
    fun onSignUpClicked(email: String, password: String, name: String, bitmap: Bitmap?) {
        var errorMsg = ""
        if (email.isEmpty()) {
            errorMsg+="Email field is missing.\n"
        }
        if (password.isEmpty()) {
            errorMsg+="Password field is missing.\n"
        }
        if (name.isEmpty()) {
            errorMsg+="Name field is missing.\n"
        }
        if (errorMsg.isNotEmpty()){
            resourceLD.postValue(Resource.OnError(errorMsg))
            return
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    goToCreateUser(it.result.user!!.uid, name, email, bitmap)
                } else {
                    resourceLD.postValue(Resource.OnError(it.exception?.message))
                }
            }
    }

    private fun goToCreateUser(uid: String, name: String, email:String, bitmap: Bitmap?) {
        bitmap?.let {
            val imageRef: StorageReference = FirebaseStorage.getInstance().reference.child("users/${uid}.jpg")
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
            val data = bos.toByteArray()
            imageRef.putBytes(data)
                .addOnSuccessListener { 
                    imageRef.downloadUrl.addOnSuccessListener { 
                        saveUserToDataBase(uid, name, email, it.toString())
                    }.addOnFailureListener {
                        resourceLD.postValue(Resource.OnError(it.message))
                    }
                }
                .addOnFailureListener{
                    resourceLD.postValue(Resource.OnError(it.message))
                }
        } ?: run {
            saveUserToDataBase(uid, name, email, null)
        }

    }

    private fun saveUserToDataBase(uid: String, name: String, email: String, url: String?) {
        val user = User(uid, name, email, url)
        DataBaseManager.saveUser(user){
            if (it.isSuccessful){
                resourceLD.postValue(Resource.OnSuccess())
            } else {
                resourceLD.postValue(Resource.OnError(it.exception?.message))

            }
        }

    }
}