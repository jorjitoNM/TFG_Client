package com.example.client.ui.navigation


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

@Serializable
object NormalNoteListDestination

@Serializable
data class NormalNoteDetailDestination (val noteId: Int)

@Serializable
object NoteMapDestination

val appDestinationList = listOf(
    NormalNoteList,NoteMap)

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

object NormalNoteList : AppMainBottomDestination {
    override val route: Any = NormalNoteListDestination
    override val title: String = "Nota Lista"
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

object NoteMap : AppMainBottomDestination {
    override val route: Any = NoteMapDestination
    override val title: String = "Nota aa"
    override val isBottomBarVisible: Boolean = true
    override val scaffoldState: ScaffoldState
        get() = ScaffoldState(
            topBarState = TopBarState(showNavigationIcon = false, arrangement = Arrangement.Start),
            fabVisible = false
        )
    override val isTopBarVisible: Boolean = false
    override val onBottomBar: Boolean = true
    override val icon: ImageVector = Icons.Filled.Place
}


