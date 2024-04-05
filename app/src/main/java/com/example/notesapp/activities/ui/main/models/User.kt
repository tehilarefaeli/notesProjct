package com.example.notesapp.activities.ui.main.models

data class User (
    val id: String = "",
    val name: String ?= "",
    val email: String ?="",
    val location: String ?= null,
    val imageUrl: String?= null
)