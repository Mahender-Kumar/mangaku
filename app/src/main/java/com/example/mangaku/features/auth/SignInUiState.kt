package com.example.mangaku.features.auth


sealed class SignInUiState {
    object Initial : SignInUiState()
    object Loading : SignInUiState()
    object Success : SignInUiState()
    data class Error(val message: String) : SignInUiState()
}