package com.example.client.domain.model.user

import com.example.client.domain.model.note.Note
import java.util.UUID

data class User(
    val id : UUID = UUID.fromString(""),
    val username : String = "",
    val password : String = "",
    val email : String = "",
    val rol : String = "",
    val notes : List<Note> = emptyList(),
    val followers : List<User> = emptyList(),
    val following : List<User> = emptyList(),
    )
