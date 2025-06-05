package com.example.client.ui.userScreen.visitor
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.client.data.model.NoteDTO
import com.example.client.data.model.UserDTO
import com.example.client.domain.model.note.NoteType
import com.example.client.ui.common.UiEvent
import com.example.client.ui.common.composables.NoteList
import com.example.client.ui.common.composables.UserStat


@Composable
fun VisitorUserScreen(
    username: String,
    showSnackbar: (String) -> Unit,
    viewModel: VisitorUserViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Carga el usuario visitante al entrar en la pantalla
    LaunchedEffect(username) {
        viewModel.handleEvent(VisitorUserEvent.LoadUser(username))
        viewModel.handleEvent(VisitorUserEvent.GetFollowers(username))
        viewModel.handleEvent(VisitorUserEvent.GetFollowing(username))
    }

    // Maneja eventos de aviso (snackbar)
    LaunchedEffect(uiState.aviso) {
        uiState.aviso?.let { event ->
            if (event is UiEvent.ShowSnackbar) {
                showSnackbar(event.message)
                viewModel.handleEvent(VisitorUserEvent.AvisoVisto)
            }
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            uiState.user?.let { user ->
                VisitorUserContent(
                    user = user,
                    notes = uiState.notes,
                    isFollowing = uiState.isFollowing,
                    followers = uiState.followers,
                    following = uiState.following,
                    onFollowClick = {
                        if (uiState.isFollowing) {
                            viewModel.handleEvent(VisitorUserEvent.Unfollow(username))
                        } else {
                            viewModel.handleEvent(VisitorUserEvent.Follow(username))
                        }
                    },
                    onFavClick = { noteId ->
                        val note = uiState.notes.find { it.id == noteId }
                        note?.let {
                            if (it.saved) {
                                viewModel.handleEvent(VisitorUserEvent.DelFavNote(noteId))
                            } else {
                                viewModel.handleEvent(VisitorUserEvent.FavNote(noteId))
                            }
                        }
                    },
                    onLikeClick = { noteId ->
                        val note = uiState.notes.find { it.id == noteId }
                        note?.let {
                            if (it.liked) {
                                viewModel.handleEvent(VisitorUserEvent.DelLikeNote(noteId))
                            } else {
                                viewModel.handleEvent(VisitorUserEvent.LikeNote(noteId))
                            }
                        }
                    }
                )

            }
        }
    }
}


@Composable
fun VisitorUserContent(
    user: UserDTO,
    notes: List<NoteDTO>,
    isFollowing: Boolean,
    followers: List<UserDTO>,
    following: List<UserDTO>,
    onFollowClick: () -> Unit,
    onFavClick: (Int) -> Unit,
    onLikeClick: (Int) -> Unit
) {
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
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.size(110.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = user.username,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onFollowClick,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFollowing) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .height(36.dp)
                        .width(120.dp)
                ) {
                    Text(
                        text = if (isFollowing) "Following" else "Follow",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 50.dp, end = 32.dp), // Ajusta el padding a tu gusto
                    horizontalArrangement = Arrangement.Center
                ) {
                    UserStat(number = user.notes.size, label = "Posts")
                    Spacer(Modifier.width(32.dp)) // Espacio entre stats
                    UserStat(number = followers.size, label = "Followers")
                    Spacer(Modifier.width(32.dp))
                    UserStat(number = following.size, label = "Following")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Notas",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
        )

        if (notes.isEmpty()) {
            // Mensaje cuando no hay notas/fotos
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "This user still doesn't have any notes.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            NoteList(
                notes = notes,
                onNoteClick = {},
                onFavClick = onFavClick,
                onLikeClick = onLikeClick
            )
        }
    }
}

class VisitorUserPreviewProvider : PreviewParameterProvider<VisitorUserPreviewData> {
    override val values = sequenceOf(
        VisitorUserPreviewData(
            user = UserDTO(
                username = "dave",
                notes = listOf(
                    NoteDTO(id = 1, title = "Granada", content = "Alhambra", type = NoteType.HISTORICAL),
                    NoteDTO(id = 2, title = "Madrid", content = "Museo del Prado", type = NoteType.CULTURAL)
                ),
            ),
            notes = listOf(
                NoteDTO(id = 1, title = "Granada", content = "Alhambra", type = NoteType.HISTORICAL),
                NoteDTO(id = 2, title = "Madrid", content = "Museo del Prado", type = NoteType.CULTURAL)
            ),
            isFollowing = false
        )
    )
}

data class VisitorUserPreviewData(
    val user: UserDTO,
    val notes: List<NoteDTO>,
    val isFollowing: Boolean
)

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun VisitorUserContentPreview(
    @PreviewParameter(VisitorUserPreviewProvider::class) previewData: VisitorUserPreviewData
) {
    VisitorUserContent(
        user = previewData.user,
        notes = previewData.notes,
        isFollowing = previewData.isFollowing,
        onFollowClick = {},
        onFavClick = {},
        onLikeClick = {},
        followers = emptyList(),
        following = emptyList()
    )
}


