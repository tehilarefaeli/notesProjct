package com.example.notesapp.activities.ui.main

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.notesapp.activities.ui.main.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream

class CreateProfileViewModel : ViewModel() {

    val resourceLD: MutableLiveData<Resource> = MutableLiveData()
    val userLD : MutableLiveData<User> = MutableLiveData()
    fun onSignUpClicked(email: String, password: String, name: String, location: String?, bitmap: Bitmap?) {
        var errorMsg = ""
        if (email.isEmpty() && userLD.value == null) {
            errorMsg+="Email field is missing.\n"
        }
        if (password.isEmpty() && userLD.value == null) {
            errorMsg+="Password field is missing.\n"
        }
        if (name.isEmpty()) {
            errorMsg+="Name field is missing.\n"
        }
        if (errorMsg.isNotEmpty()){
            resourceLD.postValue(Resource.OnError(errorMsg))
            return
        }

        if (userLD.value != null) {
            goToCreateUser(userLD.value?.id ?: "", name, email,location, bitmap)

        }else {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        goToCreateUser(it.result.user!!.uid, name, email, location, bitmap)
                    } else {
                        resourceLD.postValue(Resource.OnError(it.exception?.message))
                    }
                }
        }

    }

    init {
        FirebaseAuth.getInstance().uid?.let {
            DataBaseManager.getCurrentUser(it) {
                val user = it.result.toObject(User::class.java)
                userLD.postValue(user!!)

            }
        }

    }
    private fun goToCreateUser(uid: String, name: String, email:String, location: String?,bitmap: Bitmap?) {
        bitmap?.let {
            val imageRef: StorageReference = FirebaseStorage.getInstance().reference.child("users/${uid}.jpg")
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
            val data = bos.toByteArray()
            imageRef.putBytes(data)
                .addOnSuccessListener { 
                    imageRef.downloadUrl.addOnSuccessListener { 
                        saveUserToDataBase(uid, name, email, location, it.toString())
                    }.addOnFailureListener {
                        resourceLD.postValue(Resource.OnError(it.message))
                    }
                }
                .addOnFailureListener{
                    resourceLD.postValue(Resource.OnError(it.message))
                }
        } ?: run {
            saveUserToDataBase(uid, name, email, location, userLD?.value?.imageUrl)
        }

    }

    private fun saveUserToDataBase(uid: String, name: String, email: String, location: String?, url: String?) {
        val user = User(uid, name, email, location, url)
        DataBaseManager.saveUser(user){
            if (it.isSuccessful){
                resourceLD.postValue(Resource.OnSuccess())
            } else {
                resourceLD.postValue(Resource.OnError(it.exception?.message))

            }
        }

    }
}