package com.example.client.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.client.R
import com.example.client.ui.addNoteScreen.AddNoteScreen
import com.example.client.ui.common.BottomBar
import com.example.client.ui.common.TopBar
import com.example.client.ui.login.LoginScreen
import com.example.client.ui.normalNoteScreen.detail.NoteDetailScreen
import com.example.client.ui.normalNoteScreen.list.NoteListScreen
import com.example.client.ui.noteMap.list.NoteMapScreen
import com.example.client.ui.noteMap.search.MapSearchScreen
import com.example.client.ui.noteMap.search.SharedLocationViewModel
import com.example.client.ui.registerScreen.SignUpScreen
import com.example.client.ui.startScreen.StartScreen
import com.example.client.ui.userScreen.detail.UserScreen
import com.example.client.ui.userScreen.search.UserSearchScreen
import kotlinx.coroutines.launch

@Composable
fun Navigation () {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val sharedLocationViewModel: SharedLocationViewModel = hiltViewModel()

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
                    navController.navigate("addNote") // Navega a AddNoteScreen
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
            startDestination = StartDestination,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            composable<NormalNoteListDestination> {
                NoteListScreen(showSnackbar = { showSnackbar(it) }, onNavigateToDetail = {navController.navigate(NormalNoteDetailDestination(it))})
            }
            composable<NormalNoteDetailDestination> { backStackEntry ->
                val destination = backStackEntry.toRoute() as NormalNoteDetailDestination
                NoteDetailScreen(noteId = destination.noteId, showSnackbar = { showSnackbar(it) }, onNavigateBack = { navController.navigateUp() })
            }
            composable<NoteMapDestination> {
                NoteMapScreen(
                    showSnackbar = { showSnackbar(it) },
                    onNavigateToList = { navController.navigate(MapSearchDestination) },
                    sharedLocationViewModel = sharedLocationViewModel,
                    onAddNoteClick = { navController.navigate(AddNoteDestination) }
                )
            }
            composable<RegisterDestination> {
                SignUpScreen(showSnackbar = { showSnackbar(it)},
                    navigateToLogin =  { navController.navigate(LoginDestination) },
                    onNavigateBack = { navController.popBackStack() })
            }
            composable<StartDestination> {
                StartScreen( navigateToSignUp = { navController.navigate(RegisterDestination)},
                    navigateToLogin = { navController.navigate(LoginDestination)})
            }
            composable<LoginDestination> {
                LoginScreen( navigateToApp = { navController.navigate(NoteMapDestination)},
                    showSnackbar = { showSnackbar(it)},
                    navigateToRegister = { navController.navigate(RegisterDestination)},
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable<MapSearchDestination> {
                MapSearchScreen(
                    onNavigateBack = { navController.navigateUp() },
                    navController = navController,
                    sharedLocationViewModel = sharedLocationViewModel,
                    showSnackbar = { showSnackbar(it) }
                )
            }

            composable<UserScreenDestination> {
                UserScreen(showSnackbar = { showSnackbar(it) }
                )
            }
            composable<UserSearchDestination> {
                UserSearchScreen( showSnackbar = { showSnackbar(it) })
            }

            composable <AddNoteDestination> {
                AddNoteScreen(
                    showSnackbar = { showSnackbar(it) }, onNavigateBack = { navController.navigateUp() }
                )
            }
        }
    }
}