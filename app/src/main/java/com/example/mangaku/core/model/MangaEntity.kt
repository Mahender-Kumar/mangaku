package com.example.mangaku.core.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.mangaku.core.util.StringListConverter

@Entity(tableName = "manga_table")
@TypeConverters(StringListConverter::class)
data class MangaEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val sub_title: String,
    val status: String,
    val thumb: String,
    val summary: String,
    val authors: List<String>,
    val genres: List<String>,
    val nsfw: Boolean,
    val type: String,
    val total_chapter: Int,
    val create_at: Long,
    val update_at: Long,
    val cached_at: Long = System.currentTimeMillis()
)


fun MangaEntity.toMangaData(): MangaData {
    return MangaData(
        id = id,
        title = title,
        sub_title = sub_title,
        status = status,
        thumb = thumb,
        summary = summary,
        authors = authors,
        genres = genres,
        nsfw = nsfw,
        type = type,
        total_chapter = total_chapter,
        create_at = create_at,
        update_at = update_at
    )
}

fun MangaData.toMangaEntity(): MangaEntity {
    return MangaEntity(
        id = id,
        title = title,
        sub_title = sub_title,
        status = status,
        thumb = thumb,
        summary = summary,
        authors = authors,
        genres = genres,
        nsfw = nsfw,
        type = type,
        total_chapter = total_chapter,
        create_at = create_at,
        update_at = update_at
    )
}
