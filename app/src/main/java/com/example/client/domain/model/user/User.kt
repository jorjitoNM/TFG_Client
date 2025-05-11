package com.example.client.domain.model.user

import com.example.client.data.model.NoteDTO
import java.util.UUID

data class User(
    val id : UUID = UUID.fromString(""),
    val username : String = "",
    val password : String = "",
    val email : String = "",
    val rol : String = "",
    val notes : List<NoteDTO> = emptyList(),
    val followers : List<User> = emptyList(),
    val following : List<User> = emptyList(),
    )
