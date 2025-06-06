package com.example.client.ui.userScreen.visitor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client.common.NetworkResult
import com.example.client.domain.usecases.follow.*
import com.example.client.domain.usecases.note.*
import com.example.client.domain.usecases.social.*
import com.example.client.domain.usecases.user.GetUserInfoUseCase
import com.example.client.ui.common.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VisitorUserViewModel @Inject constructor(
    private val followUserUseCase: FollowUserUseCase,
    private val getFollowersUseCase: GetFollowersUseCase,
    private val getFollowingUseCase: GetFollowingUseCase,
    private val unfollowUserUseCase: UnfollowUserUseCase,
    private val isFollowingUseCase: IsFollowingUseCase,
    private val getUserNotesUseCase: GetUserNotesUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val favNoteUseCase: FavNoteUseCase,
    private val likeNoteUseCase: LikeNoteUseCase,
    private val delLikeNoteUseCase: DelLikeNoteUseCase,
    private val delFavNoteUseCase: DelFavNoteUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(VisitorUserState())
    val uiState = _uiState.asStateFlow()

    fun handleEvent(event: VisitorUserEvent) {
        when (event) {
            is VisitorUserEvent.LoadUser -> loadUser(event.username)
            is VisitorUserEvent.Follow -> followUser(event.username)
            is VisitorUserEvent.Unfollow -> unfollowUser(event.username)
            is VisitorUserEvent.FavNote -> favNote(event.noteId)
            is VisitorUserEvent.DelFavNote -> delSavedNote(event.noteId)
            is VisitorUserEvent.LikeNote -> likeNote(event.noteId)
            is VisitorUserEvent.DelLikeNote -> delLikedNote(event.noteId)
            is VisitorUserEvent.LoadIsFollowing -> loadIsFollowing(event.username)
            is VisitorUserEvent.AvisoVisto -> avisoVisto()
            is VisitorUserEvent.GetFollowers -> getFollowers(event.username)
            is VisitorUserEvent.GetFollowing -> getFollowing(event.username)
            is VisitorUserEvent.SelectedNote -> selectNote(event.noteId)
        }
    }

    private fun getFollowers(username: String, showLoading: Boolean = true) {
        viewModelScope.launch {
            if (showLoading) {
                _uiState.update { it.copy(isLoading = true) }
            }
            when (val result = getFollowersUseCase(username)) {
                is NetworkResult.Success -> _uiState.update { it.copy(followers = result.data, isLoading = false) }
                is NetworkResult.Error -> _uiState.update { it.copy(aviso = UiEvent.ShowSnackbar(result.message), isLoading = false) }
                is NetworkResult.Loading -> if (showLoading) _uiState.update { it.copy(isLoading = true) }
            }
        }
    }

    private fun getFollowing(username: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = getFollowingUseCase(username)) {
                is NetworkResult.Success -> _uiState.update { it.copy(following = result.data, isLoading = false) }
                is NetworkResult.Error -> _uiState.update { it.copy(aviso = UiEvent.ShowSnackbar(result.message), isLoading = false) }
                is NetworkResult.Loading -> _uiState.update { it.copy(isLoading = true) }
            }
        }
    }

    private fun favNote(noteId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = favNoteUseCase.invoke(noteId)) {
                is NetworkResult.Error -> _uiState.update {
                    it.copy(aviso = UiEvent.ShowSnackbar(result.message), isLoading = false)
                }
                is NetworkResult.Loading -> _uiState.update { it.copy(isLoading = true) }
                is NetworkResult.Success -> _uiState.update {
                    val updatedNotes = uiState.value.notes.map { note ->
                        if (note.id == noteId) note.copy(saved = true) else note
                    }
                    it.copy(isLoading = false, notes = updatedNotes)
                }
            }
        }
    }

    private fun delSavedNote(noteId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = delFavNoteUseCase.invoke(noteId)) {
                is NetworkResult.Error -> _uiState.update {
                    it.copy(aviso = UiEvent.ShowSnackbar(result.message), isLoading = false)
                }
                is NetworkResult.Loading -> _uiState.update { it.copy(isLoading = true) }
                is NetworkResult.Success -> _uiState.update {
                    val updatedNotes = uiState.value.notes.map { note ->
                        if (note.id == noteId) note.copy(saved = false) else note
                    }
                    it.copy(isLoading = false, notes = updatedNotes)
                }
            }
        }
    }

    private fun likeNote(noteId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = likeNoteUseCase.invoke(noteId)) {
                is NetworkResult.Success -> {
                    val updatedNotes = uiState.value.notes.map { note ->
                        if (note.id == noteId) note.copy(liked = true) else note
                    }
                    _uiState.update { it.copy(isLoading = false, notes = updatedNotes) }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(aviso = UiEvent.ShowSnackbar(result.message), isLoading = false)
                    }
                }
                is NetworkResult.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun delLikedNote(noteId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = delLikeNoteUseCase.invoke(noteId)) {
                is NetworkResult.Error -> _uiState.update {
                    it.copy(aviso = UiEvent.ShowSnackbar(result.message), isLoading = false)
                }
                is NetworkResult.Loading -> _uiState.update { it.copy(isLoading = true) }
                is NetworkResult.Success -> {
                    val updatedNotes = uiState.value.notes.map { note ->
                        if (note.id == noteId) note.copy(liked = false) else note
                    }
                    _uiState.update { it.copy(isLoading = false, notes = updatedNotes) }
                }
            }
        }
    }


    private fun loadUser(username: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val userResult = getUserInfoUseCase(username)
            val notesResult = getUserNotesUseCase(username)
            val isFollowingResult = isFollowingUseCase(username)

            if (userResult is NetworkResult.Success &&
                notesResult is NetworkResult.Success &&
                isFollowingResult is NetworkResult.Success
            ) {
                _uiState.update {
                    it.copy(
                        user = userResult.data, // Ahora sí es un UserDTO
                        notes = notesResult.data,
                        isFollowing = isFollowingResult.data,
                        isLoading = false
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        aviso = UiEvent.ShowSnackbar("Error cargando usuario")
                    )
                }
            }
        }
    }

    private fun followUser(username: String) {
        viewModelScope.launch {
            when (val result = followUserUseCase(username)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(isFollowing = true) }
                    getFollowers(username, showLoading = false) // <-- Actualiza la lista de seguidores
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(aviso = UiEvent.ShowSnackbar(result.message)) }
                }
                is NetworkResult.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun unfollowUser(username: String) {
        viewModelScope.launch {
            when (val result = unfollowUserUseCase(username)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(isFollowing = false) }
                    getFollowers(username, showLoading = false) // <-- Actualiza la lista de seguidores
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(aviso = UiEvent.ShowSnackbar(result.message)) }
                }
                is NetworkResult.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }


    private fun loadIsFollowing(username: String) {
        viewModelScope.launch {
            when (val result = isFollowingUseCase(username)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(isFollowing = result.data) }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(aviso = UiEvent.ShowSnackbar(result.message)) }
                }
                is NetworkResult.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun selectNote(id: Int) {
        _uiState.update {
            it.copy(
                selectedNoteId = id,
                aviso = UiEvent.PopBackStack
            )
        }
    }

    private fun avisoVisto() {
        _uiState.update { it.copy(aviso = null) }
    }
}
