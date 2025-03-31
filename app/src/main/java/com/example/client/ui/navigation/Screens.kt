package com.example.client.ui.navigation


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

@Serializable
object ValorateListDestination

@Serializable
object TestDestination

val appDestinationList_Adrian = listOf(
    ValorateList,Test)

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

object ValorateList : AppMainBottomDestination {
    override val route: Any = ValorateListDestination
    override val title: String = "Home"
    override val isBottomBarVisible: Boolean = true
    override val scaffoldState: ScaffoldState
        get() = ScaffoldState(
            topBarState = TopBarState(showNavigationIcon = false, arrangement = Arrangement.Start),
            fabVisible = false
        )
    override val isTopBarVisible: Boolean = true
    override val onBottomBar: Boolean = true
    override val icon: ImageVector = Icons.Filled.Home
}

object Test : AppMainBottomDestination {
    override val route: Any = TestDestination
    override val title: String = "Test"
    override val isBottomBarVisible: Boolean = true
    override val scaffoldState: ScaffoldState
        get() = ScaffoldState(
            topBarState = TopBarState(showNavigationIcon = false, arrangement = Arrangement.Start),
            fabVisible = false
        )
    override val isTopBarVisible: Boolean = true
    override val onBottomBar: Boolean = true
    override val icon: ImageVector = Icons.Filled.Place
}


