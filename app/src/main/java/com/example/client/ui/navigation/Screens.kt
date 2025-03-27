package com.example.client.ui.navigation


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

@Serializable
data object HomeD

val appDestinationList = listOf(
    Home)

interface AppDestination{
    val route: Any
    val title: String
    val scaffoldState: ScaffoldState
        get() = ScaffoldState(
            topBarState = TopBarState(showNavigationIcon = true, arrangement = Arrangement.Start),
            fabVisible = true
        )
    val isBottomBarVisible : Boolean
    val isTopBarVisible : Boolean
}

interface AppMainBottomDestination : AppDestination {
    val onBottomBar: Boolean
    val icon: ImageVector
}

object Home : AppMainBottomDestination {
    override val route: Any = "home"
    override val title: String = "Home"
    override val isBottomBarVisible: Boolean = true
    override val isTopBarVisible: Boolean = true
    override val onBottomBar: Boolean = true
    override val icon: ImageVector = Icons.Filled.Home
}


