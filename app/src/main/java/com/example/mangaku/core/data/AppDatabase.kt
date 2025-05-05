package com.example.mangaku.core.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mangaku.core.model.MangaEntity
import com.example.mangaku.core.model.UserEntity
import com.example.mangaku.core.util.StringListConverter

@Database(entities = [UserEntity::class, MangaEntity::class], version = 1, exportSchema = false)
@TypeConverters(StringListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    abstract fun mangaDao(): MangaDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration(false) // Handle migrations (use with caution in production)

                    .build().also { instance = it }
            }
        }
    }
}