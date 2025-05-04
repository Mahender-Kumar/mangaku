package com.example.mangaku.features.auth


import android.os.Build
import androidx.annotation.RequiresApi

import java.security.MessageDigest
import java.util.Base64

class PasswordEncryptor : PasswordEncryptorInterface {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun encrypt(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return Base64.getEncoder().encodeToString(bytes)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun verify(inputPassword: String, storedPassword: String): Boolean {
        return encrypt(inputPassword) == storedPassword
    }
}