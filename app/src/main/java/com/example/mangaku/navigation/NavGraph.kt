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
            val db = remember { AppDatabase.getDatabase(context) }
            val userDao = db.userDao()
            val passwordEncryptor = remember { PasswordEncryptor() }
            val viewModel: SignInViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                factory = SignInViewModelFactory(userDao, passwordEncryptor)
            )

            SignInScreen(viewModel = viewModel, navController = navController)
        }

        composable("manga") {
            MangaScreen(
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
            // Use the updated MangaDetailScreen that loads data from cache if needed
            MangaDetailScreen(mangaId = mangaId)
        }


    }
}
