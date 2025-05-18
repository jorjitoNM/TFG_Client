package com.example.client.data.repositories

import com.example.client.common.NetworkResult
import com.example.client.data.model.NoteDTO
import com.example.client.data.model.NoteMapDTO
import com.example.client.data.remote.datasource.NoteRemoteDataSource
import com.example.client.di.IoDispatcher
import com.example.client.domain.model.note.NoteType
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

    suspend fun getGroupedNotesByZoom(zoom: Float) = withContext(dispatcher) {
        try {
            // 1. Obtén todas las notas individuales del backend
            val notesResult = noteRemoteDataSource.getNotes()
            when (notesResult) {
                is NetworkResult.Success -> {
                    val allNotes = notesResult.data

                    // 2. Agrupa por lat/lng como string clave
                    val grouped = allNotes.groupBy { "${it.latitude},${it.longitude}" }

                    // 3. Aplica la lógica de filtrado según el zoom
                    val result = grouped.values.mapNotNull { notesAtLocation ->
                        val totalLikes = notesAtLocation.sumOf { it.likes }
                        val first = notesAtLocation.first()
                        val include = when {
                            zoom <= 5f -> totalLikes > 35
                            zoom <= 12f -> totalLikes > 10
                            else -> true
                        }
                        if (include) {
                            NoteMapDTO(
                                latitude = first.latitude,
                                longitude = first.longitude,
                                totalLikes = totalLikes,
                                notes = notesAtLocation
                            )
                        } else null
                    }
                    NetworkResult.Success(result)
                }

                is NetworkResult.Error -> {
                    NetworkResult.Error(notesResult.message ?: "Error loading notes")
                }

                else -> {
                    NetworkResult.Loading()
                }
            }
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

    suspend fun rateNote(id: Int, rating: Int) = withContext(dispatcher) {
        try {
            noteRemoteDataSource.rateNote(id, rating)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: e.toString())
        }
    }

    suspend fun updateNote(note: NoteDTO) = withContext(dispatcher) {
        try {
            noteRemoteDataSource.updateNote(note)
        } catch (e: Exception) {
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

    suspend fun deleteNote(idNote: Int) = withContext(dispatcher) {
        try {
            noteRemoteDataSource.deleteNote(idNote)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: e.toString())
        }
    }
}