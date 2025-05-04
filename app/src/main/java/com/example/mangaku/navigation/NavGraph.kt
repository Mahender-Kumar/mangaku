package com.example.mangaku.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.mangaku.core.data.AppDatabase
import com.example.mangaku.features.auth.SignInScreen
import com.example.mangaku.features.auth.SignInViewModel
import com.example.mangaku.features.auth.SignInViewModelFactory
import com.example.mangaku.features.detection.ProfileScreen
import com.example.mangaku.features.manga.HomeScreen
import com.example.mangaku.features.manga.HomeViewModel
import com.example.mangaku.features.manga.MangaDetailScreen


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable("sign_in") {
            val context = LocalContext.current
            val db = remember { AppDatabase.Companion.getDatabase(context) }
            val userDao = db.userDao()
            val viewModel: SignInViewModel = viewModel(factory = SignInViewModelFactory(userDao))

            SignInScreen(viewModel = viewModel, navController = navController)
        }

        composable("home") {


            HomeScreen(
                onMangaClick = { manga ->
                    print("Clicked on manga: ${manga.id}")
                    navController.navigate("mangaDetail/${manga.id}")
                }
            )
        }

        composable("profile") { ProfileScreen() }

        composable(
            route = "mangaDetail/{mangaId}",
            arguments = listOf(navArgument("mangaId") { type = NavType.StringType })
        ) { backStackEntry ->
            val mangaId = backStackEntry.arguments?.getString("mangaId")
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("home")
            }
            val viewModel: HomeViewModel = viewModel(parentEntry)
            val manga = viewModel.getMangaById(mangaId)


            manga?.let {
                MangaDetailScreen(it)
            } ?: Text("Manga not found")
        }


    }
}
