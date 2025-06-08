package com.example.client.data.model

import android.net.Uri
import java.util.UUID

data class UserDTO(
    val id: UUID = UUID.randomUUID(),
    val username: String = "",
    val email: String = "",
    val notes: List<NoteDTO> = emptyList(),
    val profilePhoto: Uri? = null,
)
