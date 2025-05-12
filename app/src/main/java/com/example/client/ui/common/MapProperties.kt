package com.example.client.ui.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.*
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
import com.example.client.R
import com.example.client.data.model.NoteDTO
import com.example.client.domain.model.note.NoteType
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng

@Composable
fun NotesBottomSheet(
    notes: List<NoteDTO>,
    location: LatLng?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Notes at this location",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        if (location != null) {
            Text(
                text = "Lat: ${location.latitude.format(6)}, Lng: ${location.longitude.format(6)}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Notes list
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(notes) { note ->
                NoteCard(note = note)
            }
        }
    }
}

@Composable
fun NoteCard(note: NoteDTO) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Handle click if needed */ },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image on the left
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = "Note Image",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            // Note details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = note.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                note.content?.let {
                    Text(
                        text = it,
                        fontSize = 14.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.DarkGray
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                // Show start and end times for events
                if (note.start != null && note.end != null) {
                    Text(
                        text = "Start: ${note.start}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "End: ${note.end}",
                        fontSize = 12.sp,
                        color = Color.Gray
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
    val borderColor = if (isSelected) Color.Blue else Color.LightGray
    val backgroundColor = if (isSelected) Color.Blue.copy(alpha = 0.1f) else Color.White

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
            color = if (isSelected) Color.Blue else Color.Black,
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
