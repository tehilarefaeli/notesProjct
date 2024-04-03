package com.example.notesapp.activities.ui.main

sealed class Resource {
    class OnSuccess : Resource()
    data class OnError(val error: String?) : Resource()

}