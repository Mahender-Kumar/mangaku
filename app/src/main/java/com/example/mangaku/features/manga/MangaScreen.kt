package com.example.mangaku.features.manga

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.mangaku.core.model.MangaData
import com.example.mangaku.core.util.toTitleCase

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MangaScreen(onMangaClick: (MangaData) -> Unit) {
    val context = LocalContext.current
    val viewModelFactory = remember { MangaViewModelFactory(context.applicationContext as android.app.Application) }
    val mangaViewModel: MangaViewModel = viewModel(factory = viewModelFactory)

    // Observe states
    val isLoading by mangaViewModel.isLoading.collectAsState()
    val error by mangaViewModel.error.collectAsState()
    val mangaData = mangaViewModel.mangaData

    Box(Modifier.fillMaxSize()) {
        when {
            // Show loading
            isLoading && mangaData.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            // Show error
            error != null && mangaData.isEmpty() -> {
                Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Error: ${error}", color = Color.Red)
                    Spacer(modifier = Modifier.height(16.dp))
                    FloatingActionButton(
                        onClick = { mangaViewModel.refreshMangaData() }
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            }

            // Show data
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2), // 2 columns
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(mangaData) { manga ->
                        MangaCard(
                            manga = manga,
                            onMangaClick = onMangaClick
                        )
                    }
                }

                // Add refresh button
//                FloatingActionButton(
//                    onClick = { mangaViewModel.refreshMangaData() },
//                    modifier = Modifier
//                        .align(Alignment.BottomEnd)
//                        .padding(16.dp)
//                ) {
//                    Icon(Icons.Default.Refresh, contentDescription = "Refresh")
//                }
            }
        }
    }

    // Call the function to fetch data if it's not already loaded
    LaunchedEffect(Unit) {
        mangaViewModel.fetchMangaData()
    }
}


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MangaCard(manga: MangaData, onMangaClick: (MangaData) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp) // Fixed height for all cards
            .clickable { onMangaClick(manga) },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(0.dp)) {
            GlideImage(
                model = manga.thumb,
                contentDescription = manga.summary,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp), // Set a fixed height
                contentScale = ContentScale.Crop // Ensures the image fills the space nicely
            )
            Text(
                text = manga.title.trim(),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .fillMaxWidth()
            )
            Surface(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(color = Color.Green, shape = CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = manga.status.toTitleCase(),
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        }
    }
}