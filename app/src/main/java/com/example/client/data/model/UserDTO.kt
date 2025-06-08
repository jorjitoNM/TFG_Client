package com.example.client.data.model

import java.util.UUID

data class UserDTO(
    val id: UUID = UUID.randomUUID(),
    val username: String = "",
    val password: String = "",
    val email: String = "",
    val notes: List<NoteDTO> = emptyList(),
    val profilePhoto: String = "",
)
