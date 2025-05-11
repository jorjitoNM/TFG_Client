package com.example.client.data.remote.datasource

import com.example.client.common.NetworkResult
import com.example.client.data.model.NoteDTO
import com.example.client.data.remote.service.NoteService
import com.example.client.domain.model.note.NoteType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NoteRemoteDataSource @Inject constructor(private val noteService: NoteService) :
    BaseApiResponse() {

    suspend fun getNotes() = safeApiCall { noteService.getNotes() }

    suspend fun getNote(id: Int) = safeApiCall { noteService.getNote(id) }
    suspend fun updateNote(note: NoteDTO) =
        safeApiCall { noteService.updateNote(note) }

    suspend fun rateNote(id: Int, rating: Int) =
        safeApiCall { noteService.rateNote(id, rating) }

    suspend fun favNote(id: Int, username: String) =
        safeApiCall { noteService.favNote(id, username) }


    suspend fun orderNote(asc : Boolean) = safeApiCall { noteService.orderNote(asc) }
    suspend fun deleteNote(idNote:Int)=safeApiCall { noteService.deleteNote(idNote) }

    suspend fun filterNoteByType(noteType: NoteType) = withContext(
        Dispatchers.IO) {
        try {
            val response = noteService.filterNoteByType(noteType)
            if (response.isSuccessful) {
                val notes = response.body()
                if (notes.isNullOrEmpty()) {
                    NetworkResult.Error("No se encontraron notas para el tipo seleccionado.")
                } else {
                    NetworkResult.Success(notes)
                }
            } else {
                NetworkResult.Error("Error en la respuesta del servidor: ${response.message()}")
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: e.toString())
        }
    }

}