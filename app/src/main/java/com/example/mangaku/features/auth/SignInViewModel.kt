package com.example.mangaku.features.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModel
import com.example.mangaku.core.data.UserDao
import com.example.mangaku.core.model.UserEntity
import kotlinx.coroutines.launch


class SignInViewModel(private val userDao: UserDao) : ViewModel() {
    val email = mutableStateOf("")
    val password = mutableStateOf("")

    fun onSignIn(navigateToHome: () -> Unit) {
        viewModelScope.launch {
            val user = userDao.getUserByEmail(email.value)
            if (user == null) {
                userDao.signOutAll()
                userDao.insertUser(UserEntity(email.value, password.value, isSignedIn = true))
            } else if (user.password == password.value) {
                userDao.signOutAll()
                userDao.markUserSignedIn(email.value)
            } else {
                return@launch // wrong password
            }
            navigateToHome()
        }
    }
}
