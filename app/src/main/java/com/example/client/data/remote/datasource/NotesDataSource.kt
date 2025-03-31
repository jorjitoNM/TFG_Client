package com.example.client.data.remote.datasource

import com.example.client.data.remote.api_services.NotesDao
import javax.inject.Inject

class NotesDataSource @Inject constructor(
    private val notesDao: NotesDao
) : BaseApiResponse() {

}