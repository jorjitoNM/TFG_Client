package com.example.client.ui.common.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.client.R
import com.example.client.data.model.NoteDTO
import com.example.client.domain.model.note.NoteType
import com.example.client.ui.theme.ClassicDark
import com.example.client.ui.theme.ClassicLight
import com.example.client.ui.theme.CulturalDark
import com.example.client.ui.theme.CulturalLight
import com.example.client.ui.theme.EventDark
import com.example.client.ui.theme.EventLight
import com.example.client.ui.theme.FoodDark
import com.example.client.ui.theme.FoodLight
import com.example.client.ui.theme.HistoricalDark
import com.example.client.ui.theme.HistoricalLight
import com.example.client.ui.theme.LandscapeDark
import com.example.client.ui.theme.LandscapeLight

@Composable
fun UserStat(number: Int, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 12.dp)
    ) {
        Text(
            text = number.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}


@Composable
fun NoteList(
    notes: List<NoteDTO>,
    onNoteClick: (Int) -> Unit,
    onFavClick: (Int) -> Unit,
    onLikeClick: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(notes) { note ->
                NoteCard(
                    note = note,
                    onClick = { onNoteClick(note.id) },
                    onFavClick = { onFavClick(note.id) },
                    onLikeClick = { onLikeClick(note.id) },
                )
            }
        }
    }
}

@Composable
fun NoteCard(
    note: NoteDTO,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onFavClick: () -> Unit,
    onLikeClick: () -> Unit
) {
    val isDarkMode = isSystemInDarkTheme()
    val textColor = if (isDarkMode) Color.White else Color.Black
    val dividerColor = if (isDarkMode) Color(0xFF444444) else Color(0xFFE0E0E0)
    val goldColor = Color(0xFFFFD700)
    val pinkColor = Color(0xFFFF4081)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(
                        id = when (note.type) {
                            NoteType.EVENT -> R.drawable.ic_note_event
                            NoteType.HISTORICAL -> R.drawable.ic_note_historical
                            NoteType.LANDSCAPE -> R.drawable.ic_note_landscape
                            NoteType.CULTURAL -> R.drawable.ic_note_cultural
                            NoteType.CLASSIC -> R.drawable.ic_note_classic
                            NoteType.FOOD -> R.drawable.ic_note_food
                        }
                    ),
                    contentDescription = "Tipo de nota",
                    modifier = Modifier.size(36.dp)
                )
            }


            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = textColor,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = note.content ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = textColor.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "By: ${note.ownerUsername ?: "Unknown"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = textColor.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "★ ${note.rating}/10",
                        style = MaterialTheme.typography.labelSmall,
                        color = when {
                            note.rating >= 7.5 -> Color(0xFF4CAF50)
                            note.rating >= 2.5 -> Color(0xFFFFC107)
                            else -> Color(0xFFF44336)
                        },
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                NoteTypeBadge(type = note.type)

            }

            IconButton(
                onClick = onFavClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = if (note.saved) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = "Favorito",
                    tint = if (note.saved) goldColor else textColor.copy(alpha = 0.4f),
                    modifier = Modifier.size(24.dp)
                )
            }
            IconButton(
                onClick = onLikeClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = if (note.liked) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                    contentDescription = "Me gusta",
                    tint = if (note.liked) pinkColor else textColor.copy(alpha = 0.4f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = dividerColor
        )
    }
}


@Composable
fun NoteTypeBadge(type: NoteType, modifier: Modifier = Modifier) {
    val color = noteTypeBadgeColor(type)
    val label = type.name
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(color)
            .padding(horizontal = 10.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1
        )
    }
}


@Composable
fun noteTypeBadgeColor(type: NoteType): Color {
    val isDark = isSystemInDarkTheme()
    return when (type) {
        NoteType.CLASSIC -> if (isDark) ClassicDark else ClassicLight
        NoteType.EVENT -> if (isDark) EventDark else EventLight
        NoteType.FOOD -> if (isDark) FoodDark else FoodLight
        NoteType.HISTORICAL -> if (isDark) HistoricalDark else HistoricalLight
        NoteType.LANDSCAPE -> if (isDark) LandscapeDark else LandscapeLight
        NoteType.CULTURAL -> if (isDark) CulturalDark else CulturalLight
    }
}