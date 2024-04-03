package com.example.notesapp.activities.ui.main.models

import java.util.Date


data class Note(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val timestamp: Date = Date(),
    val author: String = "" // Provide a default value for author
)