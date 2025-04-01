package com.example.client.data.remote

import com.example.client.common.NetworkResult
import com.example.client.data.remote.datasource.NoteRemoteDataSource
import com.example.client.domain.model.note.Note
import com.example.client.ui.noteScreen.list.NoteDTO
import com.example.musicapprest.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NoteRepository @Inject constructor(
    private val noteRemoteDataSource: NoteRemoteDataSource,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    suspend fun getNotes() = withContext(dispatcher) {
        try{
            noteRemoteDataSource.getNotes()
        } catch (e:Exception){
            NetworkResult.Error(e.message ?: e.toString())
        }
    }
    suspend fun getNote(id: Int) = withContext(dispatcher) {
        try{
            noteRemoteDataSource.getNote(id)
        } catch (e:Exception){
            NetworkResult.Error(e.message ?: e.toString())
        }
    }

    suspend fun rateNote(id: Int, rating: Int, username: String) = withContext(dispatcher) {
        try {
            noteRemoteDataSource.rateNote(id, rating, username)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: e.toString())
        }
    }

    suspend fun updateNote(note: NoteDTO, username: String) = withContext(dispatcher) {
        try {
            noteRemoteDataSource.updateNote(note, username)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: e.toString())
        }
    }
}