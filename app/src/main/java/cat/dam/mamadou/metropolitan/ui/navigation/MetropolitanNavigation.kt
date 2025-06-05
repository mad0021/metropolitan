package cat.dam.mamadou.metropolitan.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cat.dam.mamadou.metropolitan.R
import cat.dam.mamadou.metropolitan.ui.screen.*
import cat.dam.mamadou.metropolitan.ui.viewmodel.ConnectivityViewModel

sealed class Screen(val route: String, val titleRes: Int, val icon: ImageVector) {
    object Home : Screen("home", R.string.nav_home, Icons.Filled.Home)
    object Map : Screen("map", R.string.nav_map, Icons.Filled.Map)
    object Search : Screen("search", R.string.nav_search, Icons.Filled.Search)
    object Credits : Screen("credits", R.string.nav_credits, Icons.Filled.Info)
    object ArtworkDetail : Screen("artwork_detail/{objectId}", R.string.artwork_details, Icons.Filled.Info)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetropolitanApp(
    navController: NavHostController = rememberNavController(),
    connectivityViewModel: ConnectivityViewModel = viewModel()
) {
    val context = LocalContext.current
    val isConnected by connectivityViewModel.isConnected.collectAsState()

    LaunchedEffect(Unit) {
        connectivityViewModel.startNetworkMonitoring(context)
    }

    DisposableEffect(Unit) {
        onDispose {
            connectivityViewModel.stopNetworkMonitoring()
        }
    }

    val items = listOf(
        Screen.Home,
        Screen.Map,
        Screen.Search,
        Screen.Credits
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.app_name))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = stringResource(screen.titleRes)
                            )
                        },
                        label = { Text(stringResource(screen.titleRes)) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(isConnected = isConnected)
            }

            composable(Screen.Map.route) {
                MapScreen(
                    isConnected = isConnected,
                    onArtworkClick = { objectId ->
                        navController.navigate("artwork_detail/$objectId")
                    }
                )
            }

            composable(Screen.Search.route) {
                SearchScreen(
                    onArtworkClick = { objectId ->
                        navController.navigate("artwork_detail/$objectId")
                    }
                )
            }

            composable(Screen.Credits.route) {
                CreditsScreen(isConnected = isConnected)
            }

            composable("artwork_detail/{objectId}") { backStackEntry ->
                val objectId = backStackEntry.arguments?.getString("objectId")?.toIntOrNull()
                if (objectId != null) {
                    ArtworkDetailScreen(
                        objectId = objectId,
                        onBackClick = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }

    // Mostrar notificació de connexió
    if (!isConnected) {
        LaunchedEffect(isConnected) {
            // Aquí podríem mostrar un SnackBar o notificació
        }
    }
}