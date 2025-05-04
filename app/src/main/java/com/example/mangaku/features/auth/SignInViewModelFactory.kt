package com.example.mangaku.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mangaku.core.data.UserDao

class SignInViewModelFactory(
    private val userDao: UserDao,
    private val passwordEncryptor: PasswordEncryptor
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignInViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SignInViewModel(userDao, passwordEncryptor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
