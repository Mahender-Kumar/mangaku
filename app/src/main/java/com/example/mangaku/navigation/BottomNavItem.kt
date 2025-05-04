package com.example.mangaku.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.mangaku.R

sealed class BottomNavItem(
    val route: String,
    @DrawableRes val icon: Int,
    @StringRes val title: Int
) {
    data object Manga : BottomNavItem("manga", R.drawable.ic_home, R.string.manga)
    data object FaceRecognitionScreen : BottomNavItem("face_recognition_screen", R.drawable.ic_profile, R.string.face_recognition_screen)
}