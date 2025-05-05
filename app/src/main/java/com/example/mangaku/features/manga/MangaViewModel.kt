package com.example.mangaku.features.manga

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mangaku.core.data.AppDatabase
import com.example.mangaku.core.model.MangaData
import com.example.mangaku.core.repository.MangaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MangaViewModel(application: Application) : AndroidViewModel(application) {
    // Data state
    val mangaData = mutableStateListOf<MangaData>()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Repository instance
    private val repository: MangaRepository

    // Flag to track if data has been loaded at least once
    private var dataLoaded = false

    init {
        val database = AppDatabase.getDatabase(application)
        repository = MangaRepository(application.applicationContext, database.mangaDao())
    }

    // Expose repository for use by other components
    fun getRepository(): MangaRepository = repository

    // Function to fetch manga data with caching
    fun fetchMangaData() {
        // Don't fetch if already loading or data is already loaded
        if (_isLoading.value || dataLoaded && mangaData.isNotEmpty()) return

        _isLoading.value = true
        _error.value = null

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val data = repository.getMangaData()
                withContext(Dispatchers.Main) {
                    mangaData.clear()
                    mangaData.addAll(data)
                    dataLoaded = true
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _error.value = "Failed to load manga: ${e.message}"
                    _isLoading.value = false
                }
            }
        }
    }

    // Get a specific manga by ID (synchronous, from memory)
    fun getMangaById(id: String?): MangaData? {
        if (id == null) return null
        return mangaData.find { it.id == id }
    }

    // Get manga by ID asynchronously (checks cache if not in memory)
    suspend fun getMangaByIdAsync(id: String?): MangaData? {
        if (id == null) return null

        // First check if it's in memory
        val inMemoryManga = mangaData.find { it.id == id }
        if (inMemoryManga != null) return inMemoryManga

        // If not in memory, get from cache
        return withContext(Dispatchers.IO) {
            repository.getMangaById(id)
        }
    }

    // Force refresh - ignores cache and fetches new data
    fun refreshMangaData() {
        dataLoaded = false
        fetchMangaData()
    }
}