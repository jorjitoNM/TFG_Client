package com.example.client.data.model

data class NoteMapDTO(val latitude: Double,
                      val longitude: Double,
                      val totalLikes: Int,
                      val notes: List<NoteDTO>)
