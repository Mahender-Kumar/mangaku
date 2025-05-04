package com.example.mangaku.features.auth

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModel
import com.example.mangaku.core.data.UserDao
import com.example.mangaku.core.model.UserEntity
import kotlinx.coroutines.launch



import androidx.compose.runtime.State

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SignInViewModel(
    private val userDao: UserDao,
    private val passwordEncryptor: PasswordEncryptor
) : ViewModel() {

    private val _email = mutableStateOf("")
    val email: State<String> = _email

    private val _password = mutableStateOf("")
    val password: State<String> = _password

    private val _uiState = MutableStateFlow<SignInUiState>(SignInUiState.Initial)
    val uiState: StateFlow<SignInUiState> = _uiState.asStateFlow()

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onSignIn(navigateToMangaScreen: () -> Unit) {
        if (!validateInputs()) {
            return
        }

        _uiState.value = SignInUiState.Loading
        viewModelScope.launch {
            try {
                val user = userDao.getUserByEmail(email.value)

                if (user == null) {
                    // Create new user with encrypted password
                    val encryptedPassword = passwordEncryptor.encrypt(password.value)
                    userDao.signOutAll()
                    userDao.insertUser(
                        UserEntity(
                            email = email.value,
                            password = encryptedPassword,
                            isSignedIn = true
                        )
                    )
                    _uiState.value = SignInUiState.Success
                    navigateToMangaScreen()
                } else if (passwordEncryptor.verify(password.value, user.password)) {
                    // Correct password, sign in user
                    userDao.signOutAll()
                    userDao.markUserSignedIn(email.value)
                    _uiState.value = SignInUiState.Success
                    navigateToMangaScreen()
                } else {
                    // Wrong password
                    _uiState.value = SignInUiState.Error("Invalid email or password")
                }
            } catch (e: Exception) {
                _uiState.value = SignInUiState.Error("Sign in failed: ${e.localizedMessage}")
            }
        }
    }

    private fun validateInputs(): Boolean {
        when {
            email.value.isBlank() -> {
                _uiState.value = SignInUiState.Error("Email cannot be empty")
                return false
            }
            !isValidEmail(email.value) -> {
                _uiState.value = SignInUiState.Error("Please enter a valid email address")
                return false
            }
            password.value.isBlank() -> {
                _uiState.value = SignInUiState.Error("Password cannot be empty")
                return false
            }
            password.value.length < 6 -> {
                _uiState.value = SignInUiState.Error("Password must be at least 6 characters")
                return false
            }
        }
        return true
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()
        return email.matches(emailRegex)
    }
}