package com.example.client.data.model

import java.util.UUID

data class UserDTO(
    val id: UUID = UUID.fromString(""),
    val username: String = "",
    val password: String = "",
    val email: String = "",
    val rol: String = "",
    val notes: List<NoteDTO> = emptyList(),
    val followers: List<UserDTO> = emptyList(),
    val following: List<UserDTO> = emptyList(),
)
