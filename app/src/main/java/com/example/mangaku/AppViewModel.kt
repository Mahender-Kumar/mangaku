package com.example.mangaku

import com.example.mangaku.core.data.UserDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Create a view model to handle app state
class AppViewModel {
    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination: StateFlow<String?> = _startDestination.asStateFlow()

    suspend fun determineStartDestination(userDao: UserDao) {
        val signedInUser = userDao.getSignedInUser()
        _startDestination.value = if (signedInUser != null) "manga" else "sign_in"
    }
}