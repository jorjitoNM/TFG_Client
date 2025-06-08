package com.example.client.ui.common.composables

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.client.BuildConfig
import com.example.client.R
import com.example.client.data.model.NoteDTO
import com.example.client.domain.model.note.NoteType
import com.example.client.ui.normalNoteScreen.detail.NoteImageItem
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng

@Composable
fun NotesBottomSheet(
    notes: List<NoteDTO>,
    location: LatLng?,
    onNoteClick: (Int) -> Unit,
) {
    val isDarkMode = isSystemInDarkTheme()
    val backgroundColor = if (isDarkMode) Color(0xFF23272F) else Color.White
    val headerColor = if (isDarkMode) Color.White else Color.Black
    val subTextColor = if (isDarkMode) Color(0xFFB0B4BA) else Color.Gray

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(backgroundColor, shape = RoundedCornerShape(16.dp))
    ) {
        // Header
        Text(
            text = "Notes at this location",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = headerColor
        )
        if (location != null) {
            Text(
                text = "Lat: ${location.latitude.format(6)}, Lng: ${location.longitude.format(6)}",
                style = MaterialTheme.typography.bodyMedium,
                color = subTextColor
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Notes list
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(notes) { note ->
                var imageUri : Uri? = null
                if (note.photos.isNotEmpty()) imageUri = note.photos.first()
                NoteCard(note = note, isDarkMode = isDarkMode, onClick = { onNoteClick(note.id) }, imageUri = imageUri)
            }
        }
    }
}

@Composable
fun NoteCard(note: NoteDTO, isDarkMode : Boolean, onClick: () -> Unit, imageUri : Uri?) {
    val cardColor = if (isDarkMode) Color(0xFF2C313A) else Color.White
    val titleColor = if (isDarkMode) Color.White else Color.Black
    val contentColor = if (isDarkMode) Color(0xFFB0B4BA) else Color.DarkGray
    val dateColor = if (isDarkMode) Color(0xFF888C94) else Color.Gray
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image on the left
            if (imageUri != null)
                NoteImageItem(modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(4.dp)), index = 1, imageUri = imageUri) { }
            else {
                val resourceId = when (note.type) {
                    NoteType.CLASSIC -> R.drawable.ic_note_classic
                    NoteType.FOOD -> R.drawable.ic_note_food
                    NoteType.EVENT -> R.drawable.ic_note_event
                    NoteType.LANDSCAPE -> R.drawable.ic_note_landscape
                    NoteType.CULTURAL -> R.drawable.ic_note_cultural
                    NoteType.HISTORICAL -> R.drawable.ic_note_historical
                }
                Image(
                    painter = painterResource(id = resourceId),
                    contentDescription = "Note Type",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            // Note details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = note.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = titleColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                note.content?.let {
                    Text(
                        text = it,
                        fontSize = 14.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = contentColor
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                // Show start and end times for events
                if (note.start != null && note.end != null) {
                    Text(
                        text = "Start: ${note.start}",
                        fontSize = 12.sp,
                        color = dateColor
                    )
                    Text(
                        text = "End: ${note.end}",
                        fontSize = 12.sp,
                        color = dateColor
                    )
                }
            }
        }
    }
}

@Composable
fun FilterChip(
    noteType: NoteType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val isDarkMode = isSystemInDarkTheme()
    val borderColor = if (isSelected) Color(0xFF448AFF) else if (isDarkMode) Color(0xFF444B58) else Color.LightGray
    val backgroundColor = when {
        isSelected && isDarkMode -> Color(0xFF448AFF).copy(alpha = 0.15f)
        isSelected -> Color(0xFF448AFF).copy(alpha = 0.1f)
        isDarkMode -> Color(0xFF23272F)
        else -> Color.White
    }
    val textColor = when {
        isSelected -> Color(0xFF448AFF)
        isDarkMode -> Color.White
        else -> Color.Black
    }

    Box(
        modifier = Modifier
            .height(36.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(50)
            )
            .clickable { onClick() }
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = noteType.name.lowercase().replaceFirstChar { it.uppercase() },
            color = textColor,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 14.sp
        )
    }
}


// Extension function to format Double with specific decimal places
fun Double.format(digits: Int) = "%.${digits}f".format(this)

fun getMarkerIconRes(type: NoteType): Int = when (type) {
    NoteType.CLASSIC     -> R.drawable.ic_note_classic
    NoteType.HISTORICAL  -> R.drawable.ic_note_historical
    NoteType.FOOD        -> R.drawable.ic_note_food
    NoteType.EVENT       -> R.drawable.ic_note_event
    NoteType.LANDSCAPE   -> R.drawable.ic_note_landscape
    NoteType.CULTURAL    -> R.drawable.ic_note_cultural
}

fun getMarkerColor(type: NoteType): Float = when (type) {
    NoteType.CLASSIC     -> BitmapDescriptorFactory.HUE_BLUE         // Azul
    NoteType.HISTORICAL  -> 30f                                      // Marrón claro (aproximado)
    NoteType.FOOD        -> BitmapDescriptorFactory.HUE_ORANGE       // Naranja/Amarillo
    NoteType.EVENT       -> 330f                                     // Rosa (aproximado)
    NoteType.LANDSCAPE   -> BitmapDescriptorFactory.HUE_GREEN        // Verde
    NoteType.CULTURAL    -> 270f                                     // Morado (aproximado)
}

fun vectorToBitmap(@DrawableRes id: Int, context: Context): BitmapDescriptor {
    val vectorDrawable = ContextCompat.getDrawable(context, id)!!
    val bitmap = Bitmap.createBitmap(
        vectorDrawable.intrinsicWidth,
        vectorDrawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
    vectorDrawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}


fun getGooglePhotoUrl(photoReference: String, maxWidth: Int = 400): String =
    "https://maps.googleapis.com/maps/api/place/photo?maxwidth=$maxWidth&photoreference=$photoReference&key=${BuildConfig.GOOGLE_PLACES_API_KEY}"



