package com.example.client.ui.userScreen.detail

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.client.data.model.NoteDTO
import com.example.client.data.model.UserDTO
import com.example.client.domain.model.note.NoteType
import com.example.client.ui.common.UiEvent
import com.example.client.ui.common.composables.NoteList
import com.example.client.ui.common.composables.UserStat
import com.example.client.ui.userScreen.DetailNavigationEvent

@Composable
fun UserScreen(
    showSnackbar: (String) -> Unit,
    viewModel: UserViewModel = hiltViewModel(),
    onNavigateToNoteDetail: (Int) -> Unit,
    onNavigateToDetailObservable: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Tab y scroll locales y persistentes
    var selectedTab by rememberSaveable { mutableStateOf(UserTab.NOTES) }

    val notesListState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }
    val favoritesListState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }
    val likesListState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }

    val currentListState = when (selectedTab) {
        UserTab.NOTES -> notesListState
        UserTab.FAVORITES -> favoritesListState
        UserTab.LIKES -> likesListState
    }

    LaunchedEffect(Unit) {
        viewModel.handleEvent(UserEvent.LoadUser)
        viewModel.handleEvent(UserEvent.GetFollowers)
        viewModel.handleEvent(UserEvent.GetFollowing)
    }

    LaunchedEffect(uiState.aviso) {
        uiState.aviso?.let { event ->
            if (event is UiEvent.ShowSnackbar) {
                showSnackbar(event.message)
                viewModel.handleEvent(UserEvent.AvisoVisto)
            }
        }
    }

    LaunchedEffect(uiState.navigationEvent) {
        when (val event = uiState.navigationEvent) {
            is DetailNavigationEvent.NavigateToMyNoteDetail -> {
                onNavigateToNoteDetail(event.noteId)
                viewModel.handleEvent(UserEvent.NavigationConsumed)
            }
            is DetailNavigationEvent.NavigateToNormalNoteDetail -> {
                onNavigateToDetailObservable(event.noteId)
                viewModel.handleEvent(UserEvent.NavigationConsumed)
            }

            is DetailNavigationEvent.None -> {}
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val isDataReady = !uiState.isLoading && !uiState.isLoadingImage // <-- Ambas cargas

        if (!isDataReady) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            UserContent(
                notes = uiState.notes,
                user = uiState.user,
                followers = uiState.followers,
                following = uiState.following,
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
                    viewModel.handleEvent(UserEvent.SelectTab(tab))
                },
                onProfileImageSelected = { imageUri -> viewModel.handleEvent(UserEvent.SaveProfileImage(imageUri)) },
                onFavClick = { noteId ->
                    val note = uiState.notes.find { it.id == noteId }
                    note?.let {
                        if (it.saved) {
                            viewModel.handleEvent(UserEvent.DelFavNote(noteId))
                        } else {
                            viewModel.handleEvent(UserEvent.FavNote(noteId))
                        }
                    }
                },
                onLikeClick = { noteId ->
                    val note = uiState.notes.find { it.id == noteId }
                    note?.let {
                        if (it.liked) {
                            viewModel.handleEvent(UserEvent.DelLikeNote(noteId))
                        } else {
                            viewModel.handleEvent(UserEvent.LikeNote(noteId))
                        }
                    }
                },
                onNoteClick = { noteId ->
                    val isMyNote = uiState.selectedTab == UserTab.NOTES
                    viewModel.handleEvent(UserEvent.SelectedNote(noteId, isMyNote))
                },
                listState = currentListState,
                favorites = uiState.favorites,
                likes = uiState.likes
            )
        }
    }
}


@Composable
fun UserContent(
    notes: List<NoteDTO>,
    user: UserDTO,
    followers: List<UserDTO>,
    following: List<UserDTO>,
    selectedTab: UserTab,
    favorites: List<NoteDTO>,
    likes: List<NoteDTO>,
    onTabSelected: (UserTab) -> Unit,
    onProfileImageSelected: (Uri) -> Unit,
    onNoteClick: (Int) -> Unit,
    onFavClick: (Int) -> Unit,
    onLikeClick: (Int) -> Unit,
    listState: LazyListState
) {
    val filteredNotes = when (selectedTab) {
        UserTab.NOTES -> notes
        UserTab.FAVORITES -> favorites
        UserTab.LIKES -> likes
    }

    val pickMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> if (uri != null) onProfileImageSelected(uri) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            shadowElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        .clickable(onClick = {
                            pickMedia.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }),
                    contentAlignment = Alignment.Center
                ) {
                    if (user.profilePhoto != null) {
                        AsyncImage(
                            model = user.profilePhoto,
                            contentDescription = "Foto de perfil",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Foto de perfil",
                            modifier = Modifier.size(110.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = user.username,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 45.dp, end = 32.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    UserStat(number = user.notes.size ?: 0, label = "Posts")
                    Spacer(Modifier.width(32.dp))
                    UserStat(number = followers.size, label = "Followers")
                    Spacer(Modifier.width(32.dp))
                    UserStat(number = following.size, label = "Following")
                }

            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        TabSelector(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        NoteList(
            notes = filteredNotes,
            onNoteClick = onNoteClick,
            onFavClick = onFavClick,
            onLikeClick = onLikeClick,
            listState = listState
        )
    }
}




@Composable
fun TabSelector(
    selectedTab: UserTab,
    onTabSelected: (UserTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {

            TabButton(
                text = "Notes",
                isSelected = selectedTab == UserTab.NOTES,
                onClick = { onTabSelected(UserTab.NOTES) },
                modifier = Modifier.weight(1f)
            )
            TabButton(
                text = "Favorites",
                isSelected = selectedTab == UserTab.FAVORITES,
                onClick = { onTabSelected(UserTab.FAVORITES) },
                modifier = Modifier.weight(1f)
            )
            TabButton(
                text = "Likes",
                isSelected = selectedTab == UserTab.LIKES,
                onClick = { onTabSelected(UserTab.LIKES) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
    } else {
        Color.Transparent
    }

    Box(
        modifier = modifier
            .clickable(onClick = onClick)
            .background(backgroundColor)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            },
            style = MaterialTheme.typography.labelLarge
        )
    }
}



@Preview(name = "Portrait Mode", showBackground = true, device = Devices.PHONE)
@Composable
fun Preview() {
    UserContent(
        notes = listOf(
            NoteDTO(
                title = "sdadasdadadadadadad",
                content = "dsadadadadad",
                rating = 10,
                ownerUsername = "dad"
            ),
            NoteDTO(rating = 5),
            NoteDTO(type = NoteType.EVENT, start = "2q", end = "dad"),
            NoteDTO(),
            NoteDTO(),
            NoteDTO()
        ),
        user = UserDTO(),
        onTabSelected = {},
        selectedTab = UserTab.FAVORITES,
        onFavClick = {},
        onLikeClick = {},
        followers = emptyList(),
        following = emptyList(),
        onNoteClick = {},
        onProfileImageSelected = {},
        listState = rememberLazyListState(),
        likes = emptyList(),
        favorites = emptyList(),

    )
}
