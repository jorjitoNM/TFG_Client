package com.example.client.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.client.R
import com.example.client.ui.common.TopBar
import com.example.client.ui.noteMap.list.NoteMapScreen
import com.example.client.ui.normalNoteScreen.detail.NoteDetailScreen
import com.example.client.ui.normalNoteScreen.list.NoteListScreen
import com.example.client.ui.savedNotes.SavedScreen
import com.example.client.ui.signUpScreen.SignUpScreen
import com.example.musicapprest.ui.common.BottomBar
import kotlinx.coroutines.launch

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val showSnackbar = { message: String ->
        scope.launch {
            snackbarHostState.showSnackbar(
                message,
                duration = SnackbarDuration.Short
            )
        }
    }

    val state by navController.currentBackStackEntryAsState()

    val screen = appDestinationList.find { screen ->
        val currentRoute = state?.destination?.route?.substringBefore("/")
        val screenRoute = screen.route.toString().substringBefore("@").substringBefore("$")
        currentRoute == screenRoute
    }

    val bottomBar: @Composable () -> Unit = {
        BottomBar(
            navController = navController,
            screens = appDestinationList,
            isVisible = screen?.isBottomBarVisible ?: false
        )
    }
    val topBar: @Composable () -> Unit = {
        TopBar(
            navController = navController,
            screen = screen,
            isVisible = screen?.isTopBarVisible ?: true,
        )
    }
    val fab: @Composable () -> Unit = {
        if (screen?.scaffoldState?.fabVisible == true) {
            FloatingActionButton(
                onClick = {
                },
                modifier = Modifier.padding(dimensionResource(R.dimen.padding16)),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = bottomBar,
        topBar = topBar,
        floatingActionButton = fab
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NormalNoteListDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<NormalNoteListDestination> {
                NoteListScreen(showSnackbar = { showSnackbar(it) }, onNavigateToDetail = {navController.navigate(NormalNoteDetailDestination(it))})
            }
            composable<NormalNoteDetailDestination> { backStackEntry ->
                val destination = backStackEntry.toRoute() as NormalNoteDetailDestination
                NoteDetailScreen(noteId = destination.noteId, showSnackbar = { showSnackbar(it) }, onNavigateBack = { navController.navigateUp() })
            }
            composable<NoteMapDestination> {
                NoteMapScreen(showSnackbar = { showSnackbar(it) })
            }
            composable<NoteSavedListDestination> {
                SavedScreen(showSnackbar = { showSnackbar(it) })
            }
            composable<SignUpDestination> {
                SignUpScreen(navigateToApp = { navController.navigate(NoteMapDestination)}, showSnackbar = { showSnackbar(it)} )
            }
        }
    }
}