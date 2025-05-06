package com.example.mangaku.core.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.mangaku.core.data.MangaDao
import com.example.mangaku.core.model.MangaData
import com.example.mangaku.core.model.toMangaData
import com.example.mangaku.core.model.toMangaEntity
import com.example.mangaku.features.manga.MangaResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class MangaRepository(
    private val context: Context,
    private val mangaDao: MangaDao
) {
    private val okHttpClient = OkHttpClient()
    // Track the last fetched page
    private var currentPage = 0
    // Check if the device has internet connection
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    // Fetch manga data with caching strategy
    suspend fun getMangaData(forceRefresh: Boolean = false): List<MangaData> {
        return withContext(Dispatchers.IO) {

            currentPage = 1
            // First try to get data from the API if network is available
            if (isNetworkAvailable()) {
                try {
                    val apiData = fetchMangaFromApi(currentPage)
                    if (apiData.isNotEmpty()) {
                        // Cache the data to local database
                        val entities = apiData.map { it.toMangaEntity() }
                        if (forceRefresh) {
                            mangaDao.clearAllManga() // Clear old data if force refreshing
                        }
                        mangaDao.insertAllManga(entities)
                        return@withContext apiData
                    }
                } catch (e: Exception) {
                    // If API call fails, fall back to cached data
                    println("❌ API fetch failed: ${e.message}")
                }
            }

            // If network is not available or API call failed, get data from local cache
            val cachedData = mangaDao.getAllManga().map { it.toMangaData() }
            cachedData
        }
    }

    // Get a specific manga by ID from cache
    suspend fun getMangaById(id: String): MangaData? {
        return withContext(Dispatchers.IO) {
            mangaDao.getMangaById(id)?.toMangaData()
        }
    }

    fun resetPagination() {
        currentPage = 1
    }

    suspend fun loadMoreManga(): List<MangaData> {
        return withContext(Dispatchers.IO) {
            if (!isNetworkAvailable()) {
                return@withContext emptyList<MangaData>()
            }

            try {
                // Increment the page number
                currentPage++

                val apiData = fetchMangaFromApi(currentPage)
                if (apiData.isNotEmpty()) {
                    // Cache the additional data
                    val entities = apiData.map { it.toMangaEntity() }
                    mangaDao.insertAllManga(entities)
                    return@withContext apiData
                }
            } catch (e: Exception) {
                println("❌ Pagination API fetch failed: ${e.message}")
            }

            emptyList<MangaData>()
        }
    }
    // Fetch fresh data from API
    private suspend fun fetchMangaFromApi(page: Int): List<MangaData> {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("https://mangaverse-api.p.rapidapi.com/manga/fetch?page=$page&genres=Harem,Fantasy&nsfw=true&type=all")
                .get()
                .addHeader("x-rapidapi-key", "8e440e7281msh5fdc3cbd2a4b4f1p1c50cajsna99ffe4ea548")
                .addHeader("x-rapidapi-host", "mangaverse-api.p.rapidapi.com")
                .build()

            try {
                okHttpClient.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        println("❌ API request failed: ${response.code}")
                        return@withContext emptyList<MangaData>()
                    }

                    val json = response.body?.string()
                    if (json.isNullOrEmpty()) {
                        println("❌ Empty response from API")
                        return@withContext emptyList<MangaData>()
                    }

                    try {
                        val mangaResponse = Gson().fromJson(json, MangaResponse::class.java)
                        println("✅ Fetched ${mangaResponse.data.size} manga from API")
                        return@withContext mangaResponse.data ?: emptyList()
                    } catch (e: Exception) {
                        println("❌ JSON parsing error: ${e.message}")
                        return@withContext emptyList<MangaData>()
                    }
                }
            } catch (e: IOException) {
                println("❌ Network error: ${e.message}")
                return@withContext emptyList<MangaData>()
            }
        }
    }
}