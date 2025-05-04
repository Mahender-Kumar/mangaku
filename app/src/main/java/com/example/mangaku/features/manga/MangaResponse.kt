package com.example.mangaku.features.manga

import com.example.mangaku.core.model.MangaData

data class MangaResponse(
    val success: Boolean,
    val data: List<MangaData> // Define this class as per the response format
)


