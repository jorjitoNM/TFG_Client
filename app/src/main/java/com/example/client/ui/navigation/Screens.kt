package com.example.client.ui.navigation


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.client.ui.common.Constantes


val appDestinationList = listOf(
    NormalNoteList, NoteMap, SignUp, Login,Start ,NormalNoteDetail,MapSearch,UserScreen,AddNote, UserSearch, VisitorUserScreen, MyNoteDetail)


interface AppDestination {
    val route: Any
    val title: String
    val scaffoldState: ScaffoldState get() = ScaffoldState(
        topBarState = TopBarState(showNavigationIcon = false, arrangement = Arrangement.Start),
        fabVisible = false
    )
    val isBottomBarVisible: Boolean
    val isTopBarVisible: Boolean
}

interface AppMainBottomDestination : AppDestination {
    val onBottomBar: Boolean
    val icon: ImageVector
    val iconFilled: ImageVector
}

object NormalNoteList : AppMainBottomDestination {
    override val route: Any = NormalNoteListDestination
    override val title: String = "Home"
    override val isBottomBarVisible: Boolean = true
    override val scaffoldState: ScaffoldState
        get() = ScaffoldState(
            topBarState = TopBarState(showNavigationIcon = false, arrangement = Arrangement.Start),
            fabVisible = false
        )
    override val isTopBarVisible: Boolean = true
    override val onBottomBar: Boolean = true
    override val icon: ImageVector = Icons.Outlined.Home
    override val iconFilled: ImageVector = Icons.Filled.Home
}

object MapSearch : AppDestination{
    override val route: Any = MapSearchDestination
    override val title: String = "Map Search"
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
    override val title: String = "Note Detail"
    override val isBottomBarVisible: Boolean = false
    override val scaffoldState: ScaffoldState
        get() = ScaffoldState(
            topBarState = TopBarState(showNavigationIcon = true, arrangement = Arrangement.Start),
            fabVisible = false
        )
    override val isTopBarVisible: Boolean = true
}

object MyNoteDetail : AppDestination {
    override val route: Any = MyNoteDetailDestination
    override val title: String = "Your Note"
    override val isBottomBarVisible: Boolean = false
    override val scaffoldState: ScaffoldState
        get() = ScaffoldState(
            topBarState = TopBarState(showNavigationIcon = true, arrangement = Arrangement.Start),
            fabVisible = false
        )
    override val isTopBarVisible: Boolean = true
}




object NoteMap : AppMainBottomDestination {
    override val route: Any = NoteMapDestination
    override val title: String = "Map"
    override val isBottomBarVisible: Boolean = true
    override val scaffoldState: ScaffoldState
        get() = ScaffoldState(
            topBarState = TopBarState(showNavigationIcon = false, arrangement = Arrangement.Start),
            fabVisible = false
        )
    override val isTopBarVisible: Boolean = false
    override val onBottomBar: Boolean = true
    override val icon: ImageVector = Icons.Outlined.Place
    override val iconFilled: ImageVector = Icons.Filled.Place
}

object UserScreen : AppMainBottomDestination {
    override val route: Any = UserScreenDestination
    override val title: String = "Profile"
    override val isBottomBarVisible: Boolean = true
    override val scaffoldState: ScaffoldState
        get() = ScaffoldState(
            topBarState = TopBarState(showNavigationIcon = false, arrangement = Arrangement.Start),
            fabVisible = false
        )
    override val isTopBarVisible: Boolean = true
    override val onBottomBar: Boolean = true
    override val icon: ImageVector = Icons.Outlined.AccountCircle
    override val iconFilled: ImageVector = Icons.Filled.AccountCircle
}

object VisitorUserScreen : AppDestination {
    override val route: Any = VisitorUserScreenDestination
    override val title: String = "Profile"
    override val isBottomBarVisible: Boolean = false
    override val scaffoldState: ScaffoldState
        get() = ScaffoldState(
            topBarState = TopBarState(showNavigationIcon = true, arrangement = Arrangement.Start),
            fabVisible = false
        )
    override val isTopBarVisible: Boolean = true
}

object UserSearch : AppMainBottomDestination {
    override val route: Any = UserSearchDestination
    override val title: String = "Search"
    override val isBottomBarVisible: Boolean = true
    override val scaffoldState: ScaffoldState
        get() = ScaffoldState(
            topBarState = TopBarState(showNavigationIcon = false, arrangement = Arrangement.Start),
            fabVisible = false
        )
    override val isTopBarVisible: Boolean = true
    override val onBottomBar: Boolean = true
    override val icon: ImageVector = Icons.Outlined.Search
    override val iconFilled: ImageVector = Icons.Filled.Search
}

object AddNote: AppDestination {
    override val route: Any = AddNoteDestination
    override val title: String = "Add Note"
    override val isBottomBarVisible: Boolean = false
    override val isTopBarVisible: Boolean = true
    override val scaffoldState: ScaffoldState
        get() = ScaffoldState(
            topBarState = TopBarState(showNavigationIcon = true, arrangement = Arrangement.Start),
            fabVisible = false
        )
}



object SignUp : AppDestination {
    override val route = RegisterDestination
    override val title: String = Constantes.LOGIN
    override val isBottomBarVisible: Boolean = false
    override val isTopBarVisible: Boolean = false
}

object Start : AppDestination {
    override val route = StartDestination
    override val title: String = Constantes.START
    override val isBottomBarVisible: Boolean = false
    override val isTopBarVisible: Boolean = false
}

object Login : AppDestination {
    override val route = LoginDestination
    override val title: String = Constantes.LOGIN
    override val isBottomBarVisible: Boolean = false
    override val isTopBarVisible: Boolean = false
}
