package com.example.client.ui.common.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

fun formatDateTime(dateTimeStr: String): String {
    return try {
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val dateTime = LocalDateTime.parse(dateTimeStr, formatter)
        val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        dateTime.format(outputFormatter)
    } catch (e: Exception) {
        dateTimeStr
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