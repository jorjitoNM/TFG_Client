package com.example.client.ui.navigation


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector


val appDestinationList = listOf(
    NormalNoteList, NoteMap, NoteSavedList, SignUp, Start,NormalNoteDetail,MapSearch,UserScreen)


interface AppDestination {
    val route: Any
    val title: String
    val scaffoldState: ScaffoldState
    val isBottomBarVisible: Boolean
    val isTopBarVisible: Boolean
}

interface AppMainBottomDestination : AppDestination {
    val onBottomBar: Boolean
    val icon: ImageVector
}

object NormalNoteList : AppMainBottomDestination {
    override val route: Any = NormalNoteListDestination
    override val title: String = "Inicio"
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

object MapSearch : AppDestination{
    override val route: Any = MapSearchDestination
    override val title: String = "Busqueda"
    override val isBottomBarVisible: Boolean = false
    override val scaffoldState: ScaffoldState
        get() = ScaffoldState(
            topBarState = TopBarState(showNavigationIcon = false, arrangement = Arrangement.Start),
            fabVisible = false
        )
    override val isTopBarVisible: Boolean = false

}

object NormalNoteDetail : AppDestination {
    override val route: Any = NormalNoteDetailDestination
    override val title: String = "Nota Detalle"
    override val isBottomBarVisible: Boolean = true
    override val scaffoldState: ScaffoldState
        get() = ScaffoldState(
            topBarState = TopBarState(showNavigationIcon = false, arrangement = Arrangement.Start),
            fabVisible = false
        )
    override val isTopBarVisible: Boolean = true
}

object NoteSavedList : AppMainBottomDestination {
    override val route: Any = NoteSavedListDestination
    override val title: String = "Favoritos"
    override val isBottomBarVisible: Boolean = true
    override val scaffoldState: ScaffoldState
        get() = ScaffoldState(
            topBarState = TopBarState(showNavigationIcon = false, arrangement = Arrangement.Start),
            fabVisible = false
        )
    override val isTopBarVisible: Boolean = true
    override val onBottomBar: Boolean = true
    override val icon: ImageVector = Icons.Filled.Star
}

object NoteMap : AppMainBottomDestination {
    override val route: Any = NoteMapDestination
    override val title: String = "Mapa"
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

object UserScreen : AppMainBottomDestination {
    override val route: Any = UserScreenDestination
    override val title: String = "Perfil"
    override val isBottomBarVisible: Boolean = true
    override val scaffoldState: ScaffoldState
        get() = ScaffoldState(
            topBarState = TopBarState(showNavigationIcon = false, arrangement = Arrangement.Start),
            fabVisible = false
        )
    override val isTopBarVisible: Boolean = true
    override val onBottomBar: Boolean = true
    override val icon: ImageVector = Icons.Filled.AccountCircle
}


object SignUp : AppDestination {
    override val route = SignUpDestination
    override val title: String = Constantes.LOGIN
    override val isBottomBarVisible: Boolean = false
    override val isTopBarVisible: Boolean = false
    override val scaffoldState = ScaffoldState(
        topBarState = TopBarState(showNavigationIcon = true, arrangement = Arrangement.Start),
        fabVisible = false,
    )
}

object Start : AppDestination {
    override val route = StartDestination
    override val title: String = Constantes.START
    override val scaffoldState: ScaffoldState = ScaffoldState(
        topBarState = TopBarState(showNavigationIcon = false, arrangement = Arrangement.Start),
        fabVisible = false,
    )
    override val isBottomBarVisible: Boolean = false
    override val isTopBarVisible: Boolean = false
}

object Login : AppDestination {
    override val route = LoginDestination
    override val title: String = Constantes.LOGIN
    override val scaffoldState: ScaffoldState = ScaffoldState(
        topBarState = TopBarState(showNavigationIcon = true, arrangement = Arrangement.Start),
        fabVisible = false,
    )
    override val isBottomBarVisible: Boolean = false
    override val isTopBarVisible: Boolean = false
}
