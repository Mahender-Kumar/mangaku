package com.example.mangaku.core.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mangaku.core.model.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE isSignedIn = 1 LIMIT 1")
    suspend fun getSignedInUser(): UserEntity?

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("UPDATE users SET isSignedIn = 0")
    suspend fun signOutAll()

    @Query("UPDATE users SET isSignedIn = 1 WHERE email = :email")
    suspend fun markUserSignedIn(email: String)
}