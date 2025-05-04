package com.example.mangaku.features.auth


interface PasswordEncryptorInterface {
    fun encrypt(password: String): String
    fun verify(inputPassword: String, storedPassword: String): Boolean
}