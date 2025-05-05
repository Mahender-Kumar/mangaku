package com.example.mangaku.features.manga

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.mangaku.core.model.MangaData
import kotlinx.coroutines.launch

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MangaDetailScreen(mangaId: String) {
    val context = LocalContext.current
    val viewModelFactory = remember { MangaViewModelFactory(context.applicationContext as android.app.Application) }
    val mangaViewModel: MangaViewModel = viewModel(factory = viewModelFactory)

    var manga by remember { mutableStateOf<MangaData?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Load manga data
    LaunchedEffect(mangaId) {
        isLoading = true
        // Try to load from cache/database
        manga = mangaViewModel.getMangaByIdAsync(mangaId)
        isLoading = false
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (manga == null) {
        // Manga not found
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Manga not found")
                Spacer(modifier = Modifier.height(16.dp))
                // Add a fetch data button to reload
                androidx.compose.material3.Button(
                    onClick = {
                        mangaViewModel.fetchMangaData()
                    }
                ) {
                    Text("Reload Data")
                }
            }
        }
    } else {
        // Display manga details
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            GlideImage(
                model = manga!!.thumb,
                contentDescription = manga!!.summary,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = manga!!.title.trim(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            if (manga!!.sub_title.isNotBlank()) {
                Text(
                    text = manga!!.sub_title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = if (manga!!.status.lowercase() == "ongoing") Color.Green else Color.Red,
                            shape = CircleShape
                        )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = manga!!.status.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Genres: ${manga!!.genres.joinToString(", ")}",
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = manga!!.summary.trim(),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

// Overload for direct use with MangaData object
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MangaDetailScreen(manga: MangaData) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        GlideImage(
            model = manga.thumb,
            contentDescription = manga.summary,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = manga.title.trim(),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        if (manga.sub_title.isNotBlank()) {
            Text(
                text = manga.sub_title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = if (manga.status.lowercase() == "ongoing") Color.Green else Color.Red,
                        shape = CircleShape
                    )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = manga.status.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.labelMedium
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Genres: ${manga.genres.joinToString(", ")}",
            style = MaterialTheme.typography.bodyMedium,
            fontStyle = FontStyle.Italic
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = manga.summary.trim(),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}