package com.example.mangaku.features.manga

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import androidx.compose.runtime.mutableStateListOf
import com.example.mangaku.core.model.MangaData
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import com.google.gson.Gson
import okhttp3.Call

class MangaViewModel : ViewModel() {


    val mangaData = mutableStateListOf<MangaData>()


    fun setMangaList(list: List<MangaData>) {
        mangaData.clear()
        mangaData.addAll(list)
    }

    fun getMangaById(id: String?): MangaData? {
        return mangaData.find { it.id == id }
    }

    // Function to fetch manga data
    fun fetchMangaData() {
        // Launching a coroutine inside viewModelScope
        viewModelScope.launch(Dispatchers.IO) {
            // Make the network request using OkHttp
            val client = OkHttpClient()

            val request = Request.Builder()
                .url("https://mangaverse-api.p.rapidapi.com/manga/fetch?page=1&genres=Harem,Fantasy&nsfw=true&type=all")
                .get()
                .addHeader("x-rapidapi-key", "8e440e7281msh5fdc3cbd2a4b4f1p1c50cajsna99ffe4ea548")
                .addHeader("x-rapidapi-host", "mangaverse-api.p.rapidapi.com")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println("❌rapidapiFailed Failed: ${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) {
                            println(" rapidapiFailed Uexpected code $response")
                        } else {
                            println("✅ rapidapiFailed    $response")
                            val json = response.body?.string()

                            val mangaList = parseMangaData(json)

                            viewModelScope.launch(Dispatchers.Main) {
                                mangaData.clear()
                                mangaData.addAll(mangaList)
                            }
                        }
                    }
                }
            })
        }
    }

    private fun parseMangaData(json: String?): List<MangaData> {


        if (json.isNullOrEmpty()) return emptyList()
        return try {
            Gson().fromJson(json, MangaResponse::class.java).data ?: emptyList()
        } catch (e: Exception) {
            println("❌ JSON Parsing Error: ${e.message}")
            emptyList()
        }
    }

}

