package com.example.client.ui.common

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.client.ui.navigation.AppDestination
import com.example.client.ui.navigation.AppMainBottomDestination
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.dp

import androidx.compose.ui.platform.LocalDensity
import kotlin.math.roundToInt
import androidx.compose.animation.core.animateFloatAsState

@Composable
fun BottomBar(
    navController: NavController,
    screens: List<AppDestination>,
    isVisible: Boolean,
) {
    val blue = Color(0xFF1565C0)
    val white = Color.White

    if (isVisible) {
        val state = navController.currentBackStackEntryAsState()
        val currentDestination = state.value?.destination

        val bottomScreens = screens.filterIsInstance<AppMainBottomDestination>()
        val selectedIndex = bottomScreens.indexOfFirst { screen ->
            currentDestination?.route?.substringBefore("/") == screen.route.toString().substringBefore("@").substringBefore("$")
        }

        NavigationBar(
            containerColor = blue,
            contentColor = white,
        ) {
            bottomScreens.forEachIndexed { index, screen ->
                val selected = index == selectedIndex
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = if (selected) screen.iconFilled else screen.icon,
                            contentDescription = null,
                        )
                    },
                    label = {
                        val progress by animateFloatAsState(
                            targetValue = if (selected) 1f else 0f,
                            animationSpec = androidx.compose.animation.core.tween(durationMillis = 300),
                            label = "UnderlineProgress"
                        )

                        val density = LocalDensity.current
                        SubcomposeLayout { constraints ->
                            val textMeasurables = subcompose("text") {
                                Text(
                                    text = screen.title,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            val textPlaceables = textMeasurables.map { it.measure(constraints) }
                            val textWidth = textPlaceables.maxOfOrNull { it.width } ?: 0
                            val textHeight = textPlaceables.maxOfOrNull { it.height } ?: 0

                            val indicatorShape = RoundedCornerShape(2.dp)

                            // Animación del progreso (de 0 a 1)

                            val indicatorWidthPx = (textWidth * progress).roundToInt()

                            val indicatorMeasurables = subcompose("indicator") {
                                if (indicatorWidthPx > 0) {
                                    Box(
                                        Modifier
                                            .padding(top = 2.dp)
                                            .height(3.dp)
                                            .width(with(density) { indicatorWidthPx.toDp() })
                                            .clip(indicatorShape)
                                            .background(Color.White)
                                    )
                                }
                            }
                            val indicatorPlaceables = indicatorMeasurables.map {
                                it.measure(constraints.copy(minWidth = indicatorWidthPx, maxWidth = indicatorWidthPx))
                            }
                            val indicatorBoxHeight = indicatorPlaceables.maxOfOrNull { it.height } ?: 0

                            val layoutHeight = textHeight + indicatorBoxHeight + with(density) { 2.dp.roundToPx() }

                            layout(textWidth, layoutHeight) {
                                textPlaceables.forEach {
                                    it.placeRelative(0, 0)
                                }
                                indicatorPlaceables.forEach {
                                    it.placeRelative(0, textHeight + with(density) { 2.dp.roundToPx() })
                                }
                            }
                        }
                    },
                    selected = selected,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = white,
                        selectedTextColor = white,
                        indicatorColor = Color.Transparent,
                        unselectedIconColor = white.copy(alpha = 0.8f),
                        unselectedTextColor = white.copy(alpha = 0.8f)
                    )
                )
            }
        }
    }
}

