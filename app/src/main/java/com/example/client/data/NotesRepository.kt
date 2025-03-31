package com.example.client.data

import com.example.client.data.remote.datasource.NotesDataSource
import javax.inject.Inject

class NotesRepository @Inject constructor(
    private val notesDataSource: NotesDataSource
) {
}