package com.example.client.ui.addNoteScreen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.client.data.model.NoteDTO
import com.example.client.domain.model.note.NotePrivacy
import com.example.client.domain.model.note.NoteType
import com.example.client.ui.common.UiEvent
import com.example.client.ui.noteMap.search.SharedLocationViewModel
import com.example.client.ui.theme.*
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt


@Composable
fun AddNoteScreen(
    addNoteViewModel: AddNoteViewModel = hiltViewModel(),
    showSnackbar: (String) -> Unit = {},
    onNavigateBack: () -> Unit,
    sharedLocationViewModel: SharedLocationViewModel
) {
    val uiState by addNoteViewModel.uiState.collectAsStateWithLifecycle()
    val sharedLocation by sharedLocationViewModel.selectedLocation.collectAsState()
    val isDarkMode = isSystemInDarkTheme()

    // Actualiza la nota con la ubicación recibida del mapa
    LaunchedEffect(sharedLocation) {
        sharedLocation?.let { (lat, lon) ->
            addNoteViewModel.handleEvent(
                AddNoteEvents.EditNote(
                    uiState.note.copy(latitude = lat, longitude = lon)
                )
            )
            Timber.d("Lat: $lat, Lon: $lon")
        }
    }

    // One-shot UI events
    LaunchedEffect(uiState.uiEvent) {
        uiState.uiEvent?.let {
            when (it) {
                is UiEvent.ShowSnackbar -> {
                    showSnackbar(it.message)
                    addNoteViewModel.handleEvent(AddNoteEvents.UiNoteEventsDone)
                }
                is UiEvent.PopBackStack -> onNavigateBack()
            }
        }
    }

    if (uiState.isLoading) {
        Box(
            Modifier
                .fillMaxSize()
                .background(if (isDarkMode) Color(0xFF23272F) else Color.White),
            Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        AddNoteContent(
            note = uiState.note,
            onEdit = { note -> addNoteViewModel.handleEvent(AddNoteEvents.EditNote(note)) },
            onAddNote = { addNoteViewModel.handleEvent(AddNoteEvents.AddNoteNote) },
            isDarkMode = isDarkMode
        )
    }
}

@Composable
private fun AddNoteContent(
    modifier: Modifier = Modifier,
    note: NoteDTO,
    onEdit: (NoteDTO) -> Unit,
    onAddNote: () -> Unit,
    isDarkMode: Boolean
) {
    var localNote by remember { mutableStateOf(note) }
    LaunchedEffect(note) {
        localNote = note
    }
    val backgroundColor = if (isDarkMode) Color(0xFF23272F) else Color.White
    val primaryColor = Color(0xFF4285F4)
    val textColor = if (isDarkMode) Color(0xFFE0E0E0) else Color.Gray
    val inputTextColor = if (isDarkMode) Color.White else Color.Black
    val borderColor = if (isDarkMode) Color(0xFF3A3A3A) else Color.LightGray
    val focusedBorderColor = if (isDarkMode) primaryColor else Color.Blue

    var datePickerForStart by remember { mutableStateOf(true) } // true = start, false = end

    // Puedes usar estos campos para mostrar la fecha seleccionada
    val today = LocalDate.now()
    val minStartDate = today.minusDays(7)
    val startDate = localNote.start?.let { LocalDateTime.parse(it).toLocalDate() }
    val endDate = localNote.end?.let { LocalDateTime.parse(it).toLocalDate() }


    // Estado para la fecha seleccionada temporalmente (puedes usar LocalDate o String)
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var tempDate by remember { mutableStateOf(LocalDate.now()) }
    var tempTime by remember { mutableStateOf(LocalTime.of(0, 0)) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }


    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        //        // Contenido principal scrolleable
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 88.dp) // Espacio para el botón fijo
        ) {
            NoteTypeTabs(
                selectedType = localNote.type,
                onTypeSelected = { type ->
                    localNote = localNote.copy(type = type)
                    onEdit(localNote)
                },
                isDarkMode = isDarkMode
            )




            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Name Field
                Column {
                    Text(
                        text = "Name",
                        fontSize = 16.sp,
                        color = textColor,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = localNote.title,
                        onValueChange = {
                            localNote = localNote.copy(title = it)
                            onEdit(localNote)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = borderColor,

                            cursorColor = textColor,
                            focusedTextColor = inputTextColor,
                            unfocusedTextColor = inputTextColor
                        ),
                        textStyle = LocalTextStyle.current.copy(color = inputTextColor),
                        singleLine = true
                    )
                }

                // Description Field
                Column {
                    Text(
                        text = "Description",
                        fontSize = 16.sp,
                        color = textColor,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = localNote.content ?: "",
                        onValueChange = {
                            localNote = localNote.copy(content = it)
                            onEdit(localNote)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = borderColor,
                            cursorColor = textColor,
                            focusedTextColor = inputTextColor,
                            unfocusedTextColor = inputTextColor
                        ),
                        textStyle = LocalTextStyle.current.copy(color = inputTextColor),
                        minLines = 3
                    )
                }

                // Rating Section
                Column {
                    Text(
                        text = "Rating",
                        fontSize = 16.sp,
                        color = textColor,
                    )
                    ColorfulLinearRatingBar(
                        rating = localNote.rating,
                        onRatingChanged = { rating ->
                            localNote = localNote.copy(rating = rating)
                            onEdit(localNote)
                        }, isDarkMode = isDarkMode
                    )
                }

                PrivacyTabs(
                    selectedPrivacy = localNote.privacy,
                    onPrivacySelected = { privacy ->
                        localNote = localNote.copy(privacy = privacy)
                        onEdit(localNote)
                    },
                    isDarkMode = isDarkMode
                )

                Column {
                    Text(
                        text = "Current Date",
                        fontSize = 16.sp,
                        color = textColor,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = null,
                            tint = textColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = LocalDateTime.now()
                                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            fontSize = 16.sp,
                            color = textColor,
                        )
                    }
                }
                val darkEnabledTextColor = Color(0xFFBDBDBD)    // Gris claro para texto deshabilitado
                val darkEnabledBorderColor = Color(0xFFE0E0E0)  // Gris claro para borde deshabilitado
                val darkEnabledLabelColor = Color(0xFFBDBDBD)

                val darkDisabledTextColor = Color(0xFF636363)   // Gris oscuro para texto deshabilitado
                val darkDisabledLabelColor = Color(0xFF5C5C5C)  // Gris oscuro para label deshabilitado

// Colores para modo claro
                val lightEnabledTextColor = Color(0xFF757575)   // Gris medio para texto deshabilitado
                val lightEnabledBorderColor = Color(0xFFBDBDBD) // Gris claro para borde deshabilitado
                val lightEnabledLabelColor = Color(0xFF757575)

                val lightDisabledTextColor = Color(0xFFB0B0B0)  // Gris claro para texto deshabilitado
                val lightDisabledLabelColor = Color(0xFF9E9E9E) // Gris medio para label deshabilitado

// Selección según modo
                val enabledTextColor = if (isDarkMode) darkEnabledTextColor else lightEnabledTextColor
                val enabledBorderColor = if (isDarkMode) darkEnabledBorderColor else lightEnabledBorderColor
                val enabledLabelColor = if (isDarkMode) darkEnabledLabelColor else lightEnabledLabelColor

                val disabledTextColor = if (isDarkMode) darkDisabledTextColor else lightDisabledTextColor
                val disabledLabelColor = if (isDarkMode) darkDisabledLabelColor else lightDisabledLabelColor

                if (localNote.type == NoteType.EVENT) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Event Dates",
                            fontSize = 16.sp,
                            color = textColor,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // START DATE FIELD + HORA
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                OutlinedTextField(
                                    value = startDate?.let { formatDateForDisplay(it.toString()) } ?: "",
                                    onValueChange = {},
                                    readOnly = true,
                                    enabled = false,
                                    label = { Text("Start date", color = enabledLabelColor) },
                                    trailingIcon = {
                                        IconButton(
                                            onClick = {
                                                datePickerForStart = true
                                                showDatePicker = true
                                            }
                                        ) {
                                            Icon(Icons.Default.DateRange, contentDescription = "Pick start date", tint = enabledTextColor)
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = enabledTextColor,
                                        unfocusedTextColor = enabledTextColor,
                                        disabledTextColor = enabledTextColor,
                                        focusedBorderColor = enabledBorderColor,
                                        unfocusedBorderColor = enabledBorderColor,
                                        disabledBorderColor = enabledBorderColor,
                                        focusedLabelColor = enabledLabelColor,
                                        unfocusedLabelColor = enabledLabelColor,
                                        disabledLabelColor = enabledLabelColor,
                                        cursorColor = Color.Transparent,
                                        focusedContainerColor = backgroundColor,
                                        unfocusedContainerColor = backgroundColor,
                                        disabledContainerColor = backgroundColor
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(onClick = { showStartTimePicker = true }) {
                                    Icon(Icons.Default.Settings, contentDescription = "Pick start time", tint = enabledTextColor)
                                }
                            }

                            // END DATE FIELD + HORA
                            val isEndEnabled = startDate != null
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                OutlinedTextField(
                                    value = endDate?.let { formatDateForDisplay(it.toString()) } ?: "",
                                    onValueChange = {},
                                    readOnly = true,
                                    enabled = false,
                                    label = { Text("End date", color = if (isEndEnabled) enabledLabelColor else disabledLabelColor) },
                                    trailingIcon = {
                                        IconButton(
                                            enabled = isEndEnabled,
                                            onClick = {
                                                if (isEndEnabled) {
                                                    datePickerForStart = false
                                                    showDatePicker = true
                                                }
                                            }
                                        ) {
                                            Icon(
                                                Icons.Default.DateRange,
                                                contentDescription = "Pick end date",
                                                tint = if (isEndEnabled) enabledTextColor else disabledTextColor
                                            )
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = enabledTextColor,
                                        unfocusedTextColor = enabledTextColor,
                                        disabledTextColor = enabledTextColor,
                                        focusedBorderColor = enabledBorderColor,
                                        unfocusedBorderColor = enabledBorderColor,
                                        disabledBorderColor = enabledBorderColor,
                                        focusedLabelColor = enabledLabelColor,
                                        unfocusedLabelColor = enabledLabelColor,
                                        disabledLabelColor = enabledLabelColor,
                                        cursorColor = Color.Transparent,
                                        focusedContainerColor = backgroundColor,
                                        unfocusedContainerColor = backgroundColor,
                                        disabledContainerColor = backgroundColor
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(
                                    enabled = isEndEnabled,
                                    onClick = { if (isEndEnabled) showEndTimePicker = true }
                                ) {
                                    Icon(Icons.Default.Settings, contentDescription = "Pick end time", tint = if (isEndEnabled) enabledTextColor else disabledTextColor)
                                }
                            }
                        }
                    }
                }



                // Photos Section
                Column {
                    Text(
                        text = "Photos",
                        fontSize = 16.sp,
                        color = textColor,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    OutlinedButton(
                        onClick = { /* Handle photo selection */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = textColor
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Add Photos",
                            fontSize = 16.sp
                        )
                    }
                }


            }
        }
        val isTitleValid = localNote.title.isNotBlank()
        val isDescriptionValid = localNote.content?.isNotBlank()
        val isRatingNotZero = localNote.rating != 0

// Valida que start y end no sean nulos ni vacíos


// Valida que las fechas sean LocalDateTime válidas y que end >= start
        val areDatesValid = if (localNote.type == NoteType.EVENT) {
            try {
                val startDateTime = localNote.start?.let { LocalDateTime.parse(it) }
                val endDateTime = localNote.end?.let { LocalDateTime.parse(it) }
                startDateTime != null && endDateTime != null && !endDateTime.isBefore(startDateTime)
            } catch (e: Exception) {
                false
            }
        } else {
            true // Para otros tipos, no importa la fecha
        }


        val isAddEnabled = isTitleValid && isDescriptionValid == true && areDatesValid && isRatingNotZero



        // Botón fijo en la parte inferior
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(backgroundColor)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Button(
                onClick = onAddNote,
                enabled = isAddEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryColor,
                    disabledContainerColor = Color.Gray // o el color que prefieras para deshabilitado
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "ADD",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }

        }
    }
    if (showDatePicker) {
        val context = LocalContext.current
        LaunchedEffect(showDatePicker) {
            val dialog = DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    val pickedDate = LocalDate.of(year, month + 1, dayOfMonth)
                    if (datePickerForStart) {
                        val currentEnd = endDate
                        val clearEnd = currentEnd != null && (pickedDate.isAfter(currentEnd) || pickedDate.isEqual(currentEnd))
                        localNote = localNote.copy(
                            start = pickedDate.atStartOfDay().toString(),
                            end = if (clearEnd) null else localNote.end
                        )
                        onEdit(localNote)
                    } else {
                        if (startDate != null && !pickedDate.isBefore(startDate)) {
                            localNote = localNote.copy(end = pickedDate.atStartOfDay().toString())
                            onEdit(localNote)
                        } else {
                            // Mensaje de error si lo deseas
                        }
                    }
                    showDatePicker = false
                },
                tempDate.year,
                tempDate.monthValue - 1,
                tempDate.dayOfMonth
            )

            if (!datePickerForStart && startDate != null) {
                dialog.datePicker.minDate = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            }

            dialog.setOnCancelListener { showDatePicker = false }
            dialog.show()
        }
    }





    if (showStartTimePicker) {
        val context = LocalContext.current
        LaunchedEffect(showStartTimePicker) {
            val currentDateTime = localNote.start?.let { LocalDateTime.parse(it) } ?: LocalDateTime.now()
            val dialog = TimePickerDialog(
                context,
                { _, hour, minute ->
                    val newDateTime = currentDateTime.withHour(hour).withMinute(minute)
                    localNote = localNote.copy(start = newDateTime.toString())
                    onEdit(localNote)
                    showStartTimePicker = false
                },
                currentDateTime.hour,
                currentDateTime.minute,
                true
            )
            dialog.setOnCancelListener { showStartTimePicker = false }
            dialog.show()
        }
    }

    if (showEndTimePicker) {
        val context = LocalContext.current
        DisposableEffect(showEndTimePicker) {
            if (showEndTimePicker) {
                val currentDateTime = localNote.end?.let { LocalDateTime.parse(it) } ?: LocalDateTime.now()
                val dialog = TimePickerDialog(
                    context,
                    { _, hour, minute ->
                        val newDateTime = currentDateTime.withHour(hour).withMinute(minute)
                        localNote = localNote.copy(end = newDateTime.toString())
                        onEdit(localNote)
                        showEndTimePicker = false
                    },
                    currentDateTime.hour,
                    currentDateTime.minute,
                    true
                )
                dialog.show()
            }
            onDispose { }
        }
    }




}






@Composable
private fun PrivacyTabs(
    selectedPrivacy: NotePrivacy,
    onPrivacySelected: (NotePrivacy) -> Unit,
    isDarkMode: Boolean
) {
    val selectedColor = Color(0xFF4285F4)
    val unselectedColor = if (isDarkMode) Color(0xFFBBBBBB) else Color.Gray

    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        NotePrivacy.entries.forEach { privacy ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onPrivacySelected(privacy) }
                    .padding(top = 3.dp, bottom = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = privacy.displayName,
                    fontSize = 16.sp,
                    color = if (selectedPrivacy == privacy) selectedColor else unselectedColor,
                    fontWeight = if (selectedPrivacy == privacy) FontWeight.Medium else FontWeight.Normal
                )

                if (selectedPrivacy == privacy) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(2.dp)
                            .background(selectedColor)
                    )
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorfulLinearRatingBar(
    rating: Int,
    onRatingChanged: (Int) -> Unit,
    maxRating: Int = 10,
    modifier: Modifier = Modifier,
    isDarkMode: Boolean
) {
    fun getBarColor(value: Float): Color = when {
        value <= maxRating / 3f -> Color(0xFFE53935) // Rojo
        value <= 2 * maxRating / 3f -> Color(0xFFFF903B) // Amarillo
        else -> Color(0xFF43A047) // Verde
    }

    var sliderValue by remember { mutableStateOf(rating.toFloat()) }

    Column(
        modifier = modifier.padding(top = 10.dp, bottom = 3.dp)
    ) {
        // Calcula el progreso del slider (0f a 1f)
        val progress = (sliderValue - 1f) / (maxRating.toFloat() - 1f)

        Slider(
            value = sliderValue,
            onValueChange = {
                sliderValue = it
                // Si quieres actualizar en tiempo real con decimales, puedes mostrarlo
                // Si prefieres solo enteros, usa: onRatingChanged(it.roundToInt())
            },
            onValueChangeFinished = {
                // Aquí puedes redondear y notificar el valor final entero
                onRatingChanged(sliderValue.roundToInt())
            },
            valueRange = 1f..maxRating.toFloat(),
            steps = 0, // Slider continuo (decimales)
            colors = SliderDefaults.colors(
                thumbColor = getBarColor(sliderValue),
                activeTrackColor = Color.Transparent,
                inactiveTrackColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp),
            thumb = {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            color = getBarColor(sliderValue),
                            shape = CircleShape
                        )
                        .border(
                            width = 1.dp,
                            color = getBarColor(sliderValue),
                            shape = CircleShape
                        )
                )
            },
            track = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                color = if (!isDarkMode) Color(0xFFD7D7D7) else Color(
                                    0xFF5F5F5F
                                ),
                                shape = RoundedCornerShape(3.dp)
                            )
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .fillMaxHeight()
                            .background(
                                color = getBarColor(sliderValue),
                                shape = RoundedCornerShape(3.dp)
                            )
                    )
                }
            }
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "${sliderValue.roundToInt()} / $maxRating",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun NoteTypeTabs(
    selectedType: NoteType,
    onTypeSelected: (NoteType) -> Unit,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier
) {
    // Función para obtener el color según el tipo y el modo
    fun getTypeColor(type: NoteType, isDarkMode: Boolean): Color = when (type) {
        NoteType.CLASSIC     ->  ClassicLight
        NoteType.EVENT       ->  EventLight
        NoteType.HISTORICAL  -> if (isDarkMode) HistoricalDark2 else HistoricalLight
        NoteType.FOOD        ->  FoodLight
        NoteType.LANDSCAPE   ->  LandscapeLight
        NoteType.CULTURAL    -> if (isDarkMode) CulturalDark2 else CulturalLight
    }

    val unselectedColor = if (isDarkMode) Color(0xFFCFCCCC) else Color.Gray
    val types = NoteType.values()

    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        items(types) { type ->
            val selectedColor = getTypeColor(type, isDarkMode)
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onTypeSelected(type) }
                    .padding(start = 27.dp, end = 27.dp, top = 8.dp, bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = type.name.lowercase().replaceFirstChar { it.uppercase() },
                    fontSize = 16.sp,
                    color = if (selectedType == type) selectedColor else unselectedColor,
                    fontWeight = if (selectedType == type) FontWeight.Medium else FontWeight.Normal
                )
                if (selectedType == type) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(2.dp)
                            .background(selectedColor, RoundedCornerShape(1.dp))
                    )
                }
            }
        }
    }
}

fun formatDateForDisplay(dateString: String): String {
    return try {
        val date = LocalDate.parse(dateString)
        date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    } catch (e: Exception) {
        dateString
    }
}







        /* ---------------------------------------------------------------------------
 * PREVIEW
 * ------------------------------------------------------------------------- */
@Preview(name = "Portrait Mode", showBackground = true, device = Devices.PHONE)
@Composable
fun AddNoteScreenPreview() {
    AddNoteContent(
        note = NoteDTO(
            id = 1,
            title = "Título de ejemplo",
            content = "Contenido de ejemplo",
            privacy = NotePrivacy.PUBLIC,
            rating = 3,
            ownerUsername = "juan",
            likes = 12,
            created = "2025-05-26",
            latitude = 40.4168,
            longitude = -3.7038,
            type = NoteType.EVENT,
            start = "",
            end = "",
        ),
        onEdit = {},
        onAddNote = {},
        isDarkMode = false
    )
}