package com.example.mangaku

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.mangaku.core.data.AppDatabase
import com.example.mangaku.navigation.BottomNavItem
import com.example.mangaku.navigation.NavGraph

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppContent() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val db = remember { AppDatabase.getDatabase(context) }
    val userDao = db.userDao()

    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val signedInUser = userDao.getSignedInUser()
        startDestination = if (signedInUser != null) "manga" else "sign_in"
    }

    if (startDestination != null) {
        val items = listOf(BottomNavItem.Manga, BottomNavItem.FaceRecognitionScreen)
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val showBottomBar = currentRoute in listOf("manga", "face_recognition_screen")

        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar {
                        items.forEach { item ->
                            NavigationBarItem(
                                icon = {
                                    Icon(painterResource(id = item.icon), contentDescription = null)
                                },
                                label = { Text(stringResource(item.title)) },
                                selected = currentRoute == item.route,
                                onClick = {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.startDestinationId) {
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
            }
        ) { innerPadding ->
            NavGraph(navController, Modifier.padding(innerPadding), startDestination!!)
        }
    }
}
