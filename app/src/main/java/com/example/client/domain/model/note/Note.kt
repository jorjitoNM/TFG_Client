package com.example.client.domain.model.note

import com.example.client.domain.model.user.User
import java.time.LocalDateTime

data class Note(
    val id : Int = 0,
    val tittle : String = "",
    val content : String = "",
    val privacy : NotePrivacy = NotePrivacy.FOLLOWERS,
    val rating: Int = 5,
    val owner : User = User(),
    val created : LocalDateTime = LocalDateTime.now(),
    val latitude : Double = 0.0,
    val longitude : Double = 0.0,
    val type : NoteType = NoteType.CLASSIC,
)
