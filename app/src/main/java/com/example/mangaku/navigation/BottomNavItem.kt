package com.example.mangaku.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.mangaku.R

sealed class BottomNavItem(
    val route: String,
    @DrawableRes val icon: Int,
    @StringRes val title: Int
) {
    data object Home : BottomNavItem("home", R.drawable.ic_home, R.string.home)
    data object Profile : BottomNavItem("profile", R.drawable.ic_profile, R.string.profile)
}