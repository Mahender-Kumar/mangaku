package com.example.mangaku.core.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val email: String,
    val password: String,  // Now stores encrypted password
    val isSignedIn: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)