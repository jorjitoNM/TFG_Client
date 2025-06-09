package com.example.client.ui.userScreen.detail

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client.common.NetworkResult
import com.example.client.di.IoDispatcher
import com.example.client.data.model.UserDTO
import com.example.client.domain.usecases.follow.GetMyFollowersUseCase
import com.example.client.domain.usecases.follow.GetMyFollowingUseCase
import com.example.client.domain.usecases.note.GetMyNoteUseCase
import com.example.client.domain.usecases.social.DelFavNoteUseCase
import com.example.client.domain.usecases.social.DelLikeNoteUseCase
import com.example.client.domain.usecases.social.FavNoteUseCase
import com.example.client.domain.usecases.social.GetLikedNoteUseCase
import com.example.client.domain.usecases.social.GetNoteSavedUseCase
import com.example.client.domain.usecases.social.LikeNoteUseCase
import com.example.client.domain.usecases.user.GetUserUseCase
import com.example.client.domain.usecases.user.images.LoadProfileImageUseCase
import com.example.client.domain.usecases.user.images.SaveProfileImageUseCase
import com.example.client.ui.common.UiEvent
import com.example.client.ui.userScreen.DetailNavigationEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val getNoteSavedUseCase: GetNoteSavedUseCase,
    private val saveProfileImageUseCase: SaveProfileImageUseCase,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val favNoteUseCase: FavNoteUseCase,
    private val likeNoteUseCase: LikeNoteUseCase,
    private val delLikeNoteUseCase: DelLikeNoteUseCase,
    private val delFavNoteUseCase: DelFavNoteUseCase,
    private val getMyNote: GetMyNoteUseCase,
    private val getLikedNoteUseCase: GetLikedNoteUseCase,
    private val getMyFollowersUseCase: GetMyFollowersUseCase,
    private val getMyFollowingsUseCase: GetMyFollowingUseCase,
    private val loadProfileImageUseCase: LoadProfileImageUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserState())
    val uiState: StateFlow<UserState> = _uiState.asStateFlow()

    fun handleEvent(event: UserEvent) {
        when (event) {
            is UserEvent.LoadUser -> loadAllTabs()
            is UserEvent.AvisoVisto -> avisoVisto()
            is UserEvent.SelectTab -> {
                _uiState.value = _uiState.value.copy(selectedTab = event.tab)
            }
            is  UserEvent.GetMyNote -> getMyNotes()
            is UserEvent.DelFavNote -> delSavedNote(event.noteId)
            is UserEvent.DelLikeNote -> delLikedNote(event.noteId)
            is UserEvent.FavNote -> favNote(event.noteId)
            is UserEvent.LikeNote -> likeNote(event.noteId)
            is UserEvent.GetFollowers -> getFollowers()
            is UserEvent.GetFollowing -> getFollowing()
            is UserEvent.NavigationConsumed -> clearNavigation()
            is UserEvent.SaveScrollPosition ->  {
                _uiState.update {
                    when (event.tab) {
                        UserTab.NOTES -> it.copy(
                            notesScrollIndex = event.index,
                            notesScrollOffset = event.offset
                        )
                        UserTab.FAVORITES -> it.copy(
                            favoritesScrollIndex = event.index,
                            favoritesScrollOffset = event.offset
                        )
                        UserTab.LIKES -> it.copy(
                            likesScrollIndex = event.index,
                            likesScrollOffset = event.offset
                        )
                    }
                }
            }

            is UserEvent.SelectedNote -> selectNote(event.noteId, event.isMyNote)
            is UserEvent.SaveProfileImage -> saveProfileImage(event.imageUri)
        }
    }



    private fun getFollowers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = getMyFollowersUseCase()) {
                is NetworkResult.Success -> _uiState.update { it.copy(followers = result.data, isLoading = false) }
                is NetworkResult.Error -> _uiState.update { it.copy(aviso = UiEvent.ShowSnackbar(result.message), isLoading = false) }
                is NetworkResult.Loading -> _uiState.update { it.copy(isLoading = true) }
            }
        }
    }

    private fun getFollowing() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = getMyFollowingsUseCase()) {
                is NetworkResult.Success -> _uiState.update { it.copy(following = result.data, isLoading = false) }
                is NetworkResult.Error -> _uiState.update { it.copy(aviso = UiEvent.ShowSnackbar(result.message), isLoading = false) }
                is NetworkResult.Loading -> _uiState.update { it.copy(isLoading = true) }
            }
        }
    }

    private fun getLikedNotes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = getLikedNoteUseCase()) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            notes = result.data,
                            isLoading = false
                        )
                    }
                }

                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            aviso = UiEvent.ShowSnackbar(result.message),
                            isLoading = false
                        )
                    }
                }

                is NetworkResult.Loading -> {
                    _uiState.update {
                        it.copy(
                            isLoading = true
                        )
                    }
                }
            }
        }
    }

    private fun delSavedNote(noteId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = delFavNoteUseCase.invoke(noteId)) {
                is NetworkResult.Error -> _uiState.update {
                    it.copy(
                        aviso = UiEvent.ShowSnackbar(result.message),
                        isLoading = false
                    )
                }

                is NetworkResult.Loading -> _uiState.update {
                    it.copy(
                        isLoading = true
                    )
                }

                is NetworkResult.Success -> _uiState.update {
                    val updatedNotes = uiState.value.notes.map { note ->
                        if (note.id == noteId) {
                            note.copy(saved = false)
                        } else {
                            note
                        }
                    }.filterNot { note ->
                        note.id == noteId && uiState.value.selectedTab == UserTab.FAVORITES
                    }
                    it.copy(
                        isLoading = false,
                        notes = updatedNotes
                    )
                }
            }
        }
    }

    private fun delLikedNote(noteId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = delLikeNoteUseCase.invoke(noteId)) {
                is NetworkResult.Error -> _uiState.update {
                    it.copy(
                        aviso = UiEvent.ShowSnackbar(result.message),
                        isLoading = false
                    )
                }

                is NetworkResult.Loading -> _uiState.update {
                    it.copy(
                        isLoading = true
                    )
                }

                is NetworkResult.Success -> {
                    val updatedNotes = uiState.value.notes.map { note ->
                        if (note.id == noteId) {
                            note.copy(liked = false)
                        } else {
                            note
                        }
                    }.filterNot { note ->
                        note.id == noteId && uiState.value.selectedTab == UserTab.LIKES
                    }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            notes = updatedNotes
                        )
                    }
                }
            }
        }
    }


    private fun favNote(noteId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = favNoteUseCase.invoke(noteId)) {
                is NetworkResult.Error -> _uiState.update {
                    it.copy(
                        aviso = UiEvent.ShowSnackbar(result.message),
                        isLoading = false
                    )
                }


                is NetworkResult.Loading -> _uiState.update {
                    it.copy(
                        isLoading = true
                    )
                }

                is NetworkResult.Success -> _uiState.update {
                    val updatedNotes = uiState.value.notes.map { note ->
                        if (note.id == noteId) {
                            note.copy(saved = true)
                        } else {
                            note
                        }
                    }
                    it.copy(
                        isLoading = false,
                        notes = updatedNotes
                    )
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
                        if (note.id == noteId) {
                            note.copy(liked = true)
                        } else {
                            note
                        }
                    }
                    _uiState.update { it.copy(isLoading = false, notes = updatedNotes) }
                }

                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            aviso = UiEvent.ShowSnackbar(result.message),
                            isLoading = false
                        )
                    }
                }

                is NetworkResult.Loading -> {
                    _uiState.update {
                        it.copy(
                            isLoading = true
                        )
                    }
                }
            }
        }
    }

    private fun saveProfileImage(imagesUri: Uri) {
        viewModelScope.launch(dispatcher) {
            saveProfileImageUseCase.invoke(imagesUri,_uiState.value.user.id).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _uiState.update {
                            it.copy(
                                user = it.user.copy(
                                    profilePhoto = result.data // <-- ¡Asegúrate de que es el campo correcto!
                                ),
                                isLoading = false
                            )
                        }
                    }

                    is NetworkResult.Error -> {
                        _uiState.update {
                            it.copy(
                                aviso = UiEvent.ShowSnackbar(result.message),
                                isLoading = false
                            )
                        }
                    }

                    is NetworkResult.Loading -> {
                        _uiState.update {
                            it.copy(
                                isLoading = true
                            )
                        }
                    }
                }
            }
        }
    }




    private fun loadUser() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = getUserUseCase()) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        user = result.data,
                        selectedTab = UserTab.NOTES
                    )
                    loadUserProfileImage()
                    getMyNotes()
                }

                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        aviso = UiEvent.ShowSnackbar(result.message),
                        selectedTab = UserTab.NOTES
                    )
                }

                is NetworkResult.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    private fun loadUserProfileImage() {
        viewModelScope.launch(dispatcher) {
            _uiState.update { it.copy(isLoadingImage = true) } // <-- Usar nuevo estado

            loadProfileImageUseCase.invoke(_uiState.value.user.id).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _uiState.update {
                            it.copy(
                                user = it.user.copy(profilePhoto = result.data),
                                isLoadingImage = false // <-- Actualizar nuevo estado
                            )
                        }
                    }
                    is NetworkResult.Error -> {
                        _uiState.update {
                            it.copy(
                                aviso = UiEvent.ShowSnackbar(result.message),
                                isLoadingImage = false
                            )
                        }
                    }
                    is NetworkResult.Loading -> {
                        // No necesitamos cambiar isLoadingImage aquí
                    }
                }
            }
        }
    }


    private fun getSavedNotes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = getNoteSavedUseCase()) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            notes = result.data,
                            isLoading = false
                        )
                    }
                }

                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            aviso = UiEvent.ShowSnackbar(result.message),
                            isLoading = false
                        )
                    }
                }

                is NetworkResult.Loading -> {
                    _uiState.update {
                        it.copy(
                            isLoading = true
                        )
                    }
                }
            }
        }
    }

    private fun loadAllTabs() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val userDeferred = async { getUserUseCase() }
            val notesDeferred = async { getMyNote() }
            val favoritesDeferred = async { getNoteSavedUseCase() }
            val likesDeferred = async { getLikedNoteUseCase() }
            val followersDeferred = async { getMyFollowersUseCase() }
            val followingDeferred = async { getMyFollowingsUseCase() }

            val userResult = userDeferred.await()
            val notesResult = notesDeferred.await()
            val favoritesResult = favoritesDeferred.await()
            val likesResult = likesDeferred.await()
            val followersResult = followersDeferred.await()
            val followingResult = followingDeferred.await()

            _uiState.update {
                it.copy(
                    user = (userResult as? NetworkResult.Success)?.data ?: UserDTO(),
                    notes = (notesResult as? NetworkResult.Success)?.data ?: emptyList(),
                    favorites = (favoritesResult as? NetworkResult.Success)?.data ?: emptyList(),
                    likes = (likesResult as? NetworkResult.Success)?.data ?: emptyList(),
                    followers = (followersResult as? NetworkResult.Success)?.data ?: emptyList(),
                    following = (followingResult as? NetworkResult.Success)?.data ?: emptyList(),
                    isLoading = false
                )
            }

            if (userResult is NetworkResult.Success) {
                loadUserProfileImage()
            }
        }
    }

    private fun getMyNotes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = getMyNote()) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            notes = result.data,
                            isLoading = false
                        )
                    }
                }

                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            aviso = UiEvent.ShowSnackbar(result.message),
                            isLoading = false
                        )
                    }
                }

                is NetworkResult.Loading -> {
                    _uiState.update {
                        it.copy(
                            isLoading = true
                        )
                    }
                }
            }
        }
    }

    private fun selectNote(noteId: Int, isMyNote: Boolean) {
        _uiState.update {
            it.copy(
                navigationEvent = if (isMyNote)
                    DetailNavigationEvent.NavigateToMyNoteDetail(noteId)
                else
                    DetailNavigationEvent.NavigateToNormalNoteDetail(noteId)
            )
        }
    }

    private fun clearNavigation() {
        _uiState.update { it.copy(navigationEvent = DetailNavigationEvent.None) }
    }

    private fun avisoVisto() {
        _uiState.value = _uiState.value.copy(aviso = null)
    }
}