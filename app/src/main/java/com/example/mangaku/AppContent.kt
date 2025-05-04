package com.example.mangaku

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.mangaku.core.data.AppDatabase
import com.example.mangaku.AppViewModel
import com.example.mangaku.core.data.UserDao
import com.example.mangaku.navigation.BottomNavItem
import com.example.mangaku.navigation.NavGraph
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

//@RequiresApi(Build.VERSION_CODES.O)
//@Composable
//fun AppContent() {
//    val context = LocalContext.current
//    val navController = rememberNavController()
//    val db = remember { AppDatabase.getDatabase(context) }
//    val userDao = db.userDao()
//
//    var startDestination by remember { mutableStateOf<String?>(null) }
//
//    LaunchedEffect(Unit) {
//        val signedInUser = userDao.getSignedInUser()
//        startDestination = if (signedInUser != null) "manga" else "sign_in"
//    }
//
//    if (startDestination != null) {
//        val items = listOf(BottomNavItem.Manga, BottomNavItem.FaceRecognitionScreen)
//        val navBackStackEntry by navController.currentBackStackEntryAsState()
//        val currentRoute = navBackStackEntry?.destination?.route
//        val showBottomBar = currentRoute in listOf("manga", "face_recognition_screen")
//
//        Scaffold(
//            bottomBar = {
//                if (showBottomBar) {
//                    NavigationBar {
//                        items.forEach { item ->
//                            NavigationBarItem(
//                                icon = {
//                                    Icon(painterResource(id = item.icon), contentDescription = null)
//                                },
//                                label = { Text(stringResource(item.title)) },
//                                selected = currentRoute == item.route,
//                                onClick = {
//                                    navController.navigate(item.route) {
//                                        popUpTo(navController.graph.startDestinationId) {
//                                            saveState = true
//                                        }
//                                        launchSingleTop = true
//                                        restoreState = true
//                                    }
//                                }
//                            )
//                        }
//                    }
//                }
//            }
//        ) { innerPadding ->
//            NavGraph(navController, Modifier.padding(innerPadding), startDestination!!)
//        }
//    }
//}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppContent() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val db = remember { AppDatabase.getDatabase(context) }
    val userDao = db.userDao()
    val appViewModel = remember { AppViewModel() }

    val startDestination by appViewModel.startDestination.collectAsState()

    // Routes that shouldn't show the bottom bar
    val noBottomBarRoutes = rememberSaveable { setOf("sign_in", "mangaDetail") }

    LaunchedEffect(Unit) {
        appViewModel.determineStartDestination(userDao)
    }

    if (startDestination == null) {
        // Show loading state
        Box(modifier = Modifier, contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        val items = listOf(BottomNavItem.Manga, BottomNavItem.FaceRecognitionScreen)
        val navBackStackEntry by navController.currentBackStackEntryAsState()

        // Check if current route should show bottom bar
        val showBottomBar = navBackStackEntry?.destination?.route?.let { route ->
            noBottomBarRoutes.none { route.startsWith(it) }
        } ?: false

        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar {
                        items.forEach { item ->
                            val selected = navBackStackEntry?.destination?.hierarchy?.any {
                                it.route == item.route
                            } ?: false

                            NavigationBarItem(
                                icon = {
                                    Icon(painterResource(id = item.icon), contentDescription = null)
                                },
                                label = { Text(stringResource(item.title)) },
                                selected = selected,
                                onClick = {
                                    // Prevent redundant navigation
                                    if (!selected) {
                                        navController.navigate(item.route) {
                                            popUpTo(navController.graph.startDestinationId) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavGraph(
                navController = navController,
                modifier = Modifier.padding(innerPadding),
                startDestination = startDestination!!
            )
        }
    }
}

