package com.example.client.data.repositories

import com.example.client.common.NetworkResult
import com.example.client.data.remote.datasource.NoteRemoteDataSource
import com.example.client.domain.model.note.NoteType
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
        try {
            noteRemoteDataSource.getNotes()
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: e.toString())
        }
    }

    suspend fun getNote(id: Int) = withContext(dispatcher) {
        try {
            noteRemoteDataSource.getNote(id)
        } catch (e: Exception) {
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

    suspend fun favNote(id: Int, username: String) = withContext(dispatcher) {
        try {
            noteRemoteDataSource.favNote(id,username)
        } catch (e: Exception){
            NetworkResult.Error(e.message ?: e.toString())
        }
    }

    suspend fun orderNote(asc: Boolean) = withContext(dispatcher) {
        try {
            noteRemoteDataSource.orderNote(asc)
        } catch (e: Exception){
            NetworkResult.Error(e.message ?: e.toString())
        }

    }

    suspend fun filterNoteByType(noteType: NoteType)= withContext(dispatcher){
        try{
            noteRemoteDataSource.filterNoteByType(noteType)
        }catch (e: Exception){
            NetworkResult.Error(e.message ?: e.toString())
        }
    }
}