package com.example.mangaku.core.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mangaku.core.model.MangaData
import com.example.mangaku.core.model.MangaEntity

@Dao
interface MangaDao {
    @Query("SELECT * FROM manga_table")
    suspend fun getAllManga(): List<MangaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllManga(manga: List<MangaEntity>)

    @Query("SELECT * FROM manga_table WHERE id = :mangaId")
    suspend fun getMangaById(mangaId: String): MangaEntity?

    @Query("DELETE FROM manga_table")
    suspend fun clearAllManga()

    @Query("SELECT COUNT(*) FROM manga_table")
    suspend fun getMangaCount(): Int
}
