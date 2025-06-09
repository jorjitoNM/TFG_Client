package com.example.client.ui.userScreen.visitor
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.client.data.model.NoteDTO
import com.example.client.data.model.UserDTO
import com.example.client.domain.model.note.NotePrivacy
import com.example.client.domain.model.note.NoteType
import com.example.client.ui.common.UiEvent
import com.example.client.ui.common.composables.NoteList
import com.example.client.ui.common.composables.UserStat


@Composable
fun VisitorUserScreen(
    username: String,
    showSnackbar: (String) -> Unit,
    viewModel: VisitorUserViewModel = hiltViewModel(),
    onNavigateToNoteDetail: (Int) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    // Scroll persistente para la lista de notas públicas
    val notesListState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }

    LaunchedEffect(username) {
        viewModel.handleEvent(VisitorUserEvent.LoadUser(username))
        viewModel.handleEvent(VisitorUserEvent.GetFollowers(username))
        viewModel.handleEvent(VisitorUserEvent.GetFollowing(username))
    }

    LaunchedEffect(uiState.aviso) {
        uiState.aviso?.let { event ->
            if (event is UiEvent.ShowSnackbar) {
                showSnackbar(event.message)
                viewModel.handleEvent(VisitorUserEvent.AvisoVisto)
            } else if (event is UiEvent.PopBackStack) {
                onNavigateToNoteDetail(uiState.selectedNoteId)
                viewModel.handleEvent(VisitorUserEvent.AvisoVisto)
            }
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when {
            uiState.user == null && uiState.isLoading -> {
                VisitorUserSkeleton()
            }
            uiState.user != null -> {
                VisitorUserContent(
                    user = uiState.user!!,
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
                    },
                    onNoteClick = { noteId ->
                        viewModel.handleEvent(VisitorUserEvent.SelectedNote(noteId))
                    },
                    notesListState = notesListState // <-- Pasa el estado de scroll
                )
                // Loader pequeño en la esquina si está cargando
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .size(32.dp)
                    )
                }
            }
            else -> {
                VisitorUserSkeleton()
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
    onNoteClick: (Int) -> Unit,
    onLikeClick: (Int) -> Unit,
    notesListState: LazyListState // <-- Recibe el estado de scroll

) {
    val publicNotes = notes.filter { it.privacy == NotePrivacy.PUBLIC }

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
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
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
                        .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    UserStat(
                        number = user.notes.size ?: 0,
                        label = "Posts",
                        modifier = Modifier.weight(1f)
                    )
                    UserStat(
                        number = followers.size,
                        label = "Followers",
                        modifier = Modifier.weight(1f)
                    )
                    UserStat(
                        number = following.size,
                        label = "Following",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Notes",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
        )

        if (publicNotes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "This user still doesn't have any notes to show.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            NoteList(
                notes = publicNotes,
                onNoteClick = onNoteClick,
                onFavClick = onFavClick,
                onLikeClick = onLikeClick,
                listState = notesListState
            )
        }
    }
}

@Composable
fun VisitorUserSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Card principal (Surface)
        Surface(
            modifier = Modifier.fillMaxWidth(),
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
                // Foto de perfil (círculo gris)
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.Gray.copy(alpha = 0.15f)),
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Nombre (caja rectangular)
                Box(
                    Modifier
                        .height(28.dp)
                        .width(140.dp)
                        .background(Color.Gray.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Botón Follow (rectángulo redondeado)
                Box(
                    Modifier
                        .height(36.dp)
                        .width(120.dp)
                        .background(Color.Gray.copy(alpha = 0.15f), RoundedCornerShape(50))
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 50.dp, end = 32.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(3) { idx ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            // Número (caja pequeña)
                            Box(
                                Modifier
                                    .height(20.dp)
                                    .width(36.dp)
                                    .background(Color.Gray.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                            )
                            Spacer(Modifier.height(8.dp))
                            // Label (caja más fina)
                            Box(
                                Modifier
                                    .height(12.dp)
                                    .width(48.dp)
                                    .background(Color.Gray.copy(alpha = 0.10f), RoundedCornerShape(6.dp))
                            )
                        }
                        if (idx < 2) Spacer(Modifier.width(32.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Título "Notas"
        Box(
            Modifier
                .height(24.dp)
                .width(80.dp)
                .padding(start = 8.dp, bottom = 8.dp)
                .background(Color.Gray.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Lista de notas (simuladas)
        repeat(3) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(vertical = 8.dp)
                    .background(Color.Gray.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
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
        following = emptyList(),
        onNoteClick = {},
        notesListState = rememberLazyListState()
    )
}


