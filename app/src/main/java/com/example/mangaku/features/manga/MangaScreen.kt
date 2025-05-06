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
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.mangaku.core.model.MangaData
import com.example.mangaku.core.util.toTitleCase

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MangaScreen(
    viewModel: MangaViewModel,
    onMangaClick: (MangaData) -> Unit
) {
    // Observe states
    val isLoading by viewModel.isLoading.collectAsState()
    val isPaginationLoading by viewModel.isPaginationLoading.collectAsState()
    val isEndReached by viewModel.isEndReached.collectAsState()
    val error by viewModel.error.collectAsState()
    val mangaData = viewModel.mangaData

    // Grid state to detect scroll position
    val gridState = rememberLazyGridState()

    // Detect when we're near the bottom for pagination
    val shouldLoadMore = remember {
        derivedStateOf {
            val layoutInfo = gridState.layoutInfo
            val visibleItemsInfo = layoutInfo.visibleItemsInfo
            if (visibleItemsInfo.isEmpty()) {
                false
            } else {
                val lastVisibleItem = visibleItemsInfo.last()
                val lastIndex = lastVisibleItem.index
                // Load more when we're 5 items away from the end
                lastIndex >= layoutInfo.totalItemsCount - 5
            }
        }
    }

    // Add this - detect when scrolled to the very bottom
    val isScrolledToBottom = remember {
        derivedStateOf {
            if (mangaData.isEmpty()) {
                false
            } else {
                val layoutInfo = gridState.layoutInfo
                val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                val totalItemsCount = layoutInfo.totalItemsCount

                // Consider "at bottom" when the last item is visible
                lastVisibleItemIndex >= totalItemsCount - 1
            }
        }
    }

    // Trigger pagination loading when near the bottom
    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value && !isEndReached && !isPaginationLoading && !isLoading && mangaData.isNotEmpty()) {
            viewModel.loadMoreManga()
        }
    }

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
                        onClick = { viewModel.refreshMangaData() }
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            }

            // Show data
            else -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    LazyVerticalGrid(
                        state = gridState,
                        columns = GridCells.Fixed(2), // 2 columns
                        modifier = Modifier.weight(1f),
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

                        // Add this to show end message at the bottom when needed
                        if (isEndReached && !isPaginationLoading) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                if (isScrolledToBottom.value) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "No more manga to load",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Show pagination loading indicator at the bottom
                    if (isPaginationLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    }
                }

                // Add refresh button
//                FloatingActionButton(
//                    onClick = { viewModel.refreshMangaData() },
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
        viewModel.fetchMangaData()
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
                val statusColor = when (manga.status.lowercase()) {
                    "ongoing" -> Color.Green
                    "completed" -> Color.Blue
                    "hiatus" -> Color.Yellow
                    "discontinued" -> Color.Red
                    else -> Color.Gray
                }
                Row(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(color = statusColor, shape = CircleShape)
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