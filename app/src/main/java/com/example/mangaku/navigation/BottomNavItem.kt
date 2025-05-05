package com.example.mangaku.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.mangaku.R

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    @StringRes val title: Int
) {
    data object Manga : BottomNavItem("manga",   Icons.Default.Menu, R.string.manga)
    data object FaceRecognitionScreen : BottomNavItem("face_recognition_screen", Icons.Default.AccountBox, R.string.face_recognition_screen)
}