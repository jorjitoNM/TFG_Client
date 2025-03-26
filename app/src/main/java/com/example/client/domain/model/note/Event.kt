package com.example.client.domain.model.note

import java.time.LocalDateTime

data class Event (
    val note : Note = Note(),
    val start : LocalDateTime = LocalDateTime.now(),
    val end : LocalDateTime = LocalDateTime.now(),
)
