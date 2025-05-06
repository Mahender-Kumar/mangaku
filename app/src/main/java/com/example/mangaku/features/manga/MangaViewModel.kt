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

    // Pagination loading state
    private val _isPaginationLoading = MutableStateFlow(false)
    val isPaginationLoading: StateFlow<Boolean> = _isPaginationLoading.asStateFlow()

    // End of data reached state
    private val _isEndReached = MutableStateFlow(false)
    val isEndReached: StateFlow<Boolean> = _isEndReached.asStateFlow()

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

    // Function to fetch initial manga data with caching
    fun fetchMangaData(forceRefresh: Boolean = false) {
        // Don't fetch if already loading
        if (_isLoading.value) return
        // Don't fetch if data is already loaded and not forcing refresh
        if (dataLoaded && mangaData.isNotEmpty() && !forceRefresh) return

        _isLoading.value = true
        _error.value = null
        // Reset end reached state when fetching initial data
        _isEndReached.value = false

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val data = repository.getMangaData(forceRefresh)
                withContext(Dispatchers.Main) {
                    mangaData.clear()
                    mangaData.addAll(data)
                    dataLoaded = true
                    _isLoading.value = false

                    // If initial fetch returns no data, consider it the end
                    if (data.isEmpty()) {
                        _isEndReached.value = true
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _error.value = "Failed to load manga: ${e.message}"
                    _isLoading.value = false
                }
            }
        }
    }

    // Function to load more data for pagination
    fun loadMoreManga() {
        // Don't load more if already loading pagination, initial loading, or end is reached
        if (_isPaginationLoading.value || _isLoading.value || _isEndReached.value) return

        _isPaginationLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val newData = repository.loadMoreManga()
                withContext(Dispatchers.Main) {
                    // Add new data to the existing list
                    if (newData.isNotEmpty()) {
                        mangaData.addAll(newData)
                    } else {
                        // If no new data is returned, we've reached the end
                        _isEndReached.value = true
                    }
                    _isPaginationLoading.value = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _error.value = "Failed to load more manga: ${e.message}"
                    _isPaginationLoading.value = false
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
        repository.resetPagination()
        fetchMangaData(forceRefresh = true)
    }
}