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
import com.example.mangaku.features.auth.PasswordEncryptor
import com.example.mangaku.features.auth.SignInScreen
import com.example.mangaku.features.auth.SignInViewModel
import com.example.mangaku.features.auth.SignInViewModelFactory
import com.example.mangaku.features.detection.FaceRecognitionScreen
import com.example.mangaku.features.manga.MangaScreen
import com.example.mangaku.features.manga.MangaViewModel
import com.example.mangaku.features.manga.MangaViewModelFactory
import com.example.mangaku.features.manga.MangaDetailScreen


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String
) {
    // Create the application context once
    val context = LocalContext.current
    val application = remember { context.applicationContext as android.app.Application }

    // Create MangaViewModel with the application scope
    // This ensures it persists across navigation events
    val mangaViewModelFactory = remember { MangaViewModelFactory(application) }
    val mangaViewModel: MangaViewModel = viewModel(factory = mangaViewModelFactory)

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable("sign_in") {
            val db = remember { AppDatabase.getDatabase(context) }
            val userDao = db.userDao()
            val passwordEncryptor = remember { PasswordEncryptor() }
            val signInViewModel: SignInViewModel = viewModel(
                factory = SignInViewModelFactory(userDao, passwordEncryptor)
            )

            SignInScreen(viewModel = signInViewModel, navController = navController)
        }

        composable("manga") {
            // Pass the shared mangaViewModel instead of creating a new one
            MangaScreen(
                viewModel = mangaViewModel,
                onMangaClick = { manga ->
                    navController.navigate("mangaDetail/${manga.id}")
                }
            )
        }

        composable("face_recognition_screen") {
            FaceRecognitionScreen()
        }

        composable(
            route = "mangaDetail/{mangaId}",
            arguments = listOf(navArgument("mangaId") { type = NavType.StringType })
        ) { backStackEntry ->
            val mangaId = backStackEntry.arguments?.getString("mangaId") ?: ""
            // Pass the shared mangaViewModel to the detail screen
            MangaDetailScreen(
                mangaId = mangaId,
                mangaViewModel = mangaViewModel
            )
        }
    }
}