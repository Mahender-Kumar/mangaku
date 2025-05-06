package com.example.mangaku.features.manga

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.mangaku.core.model.MangaData
import com.example.mangaku.core.util.toTitleCase
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun MangaDetailScreen(
    mangaId: String,
    mangaViewModel: MangaViewModel
) {
    val context = LocalContext.current
    var manga by remember { mutableStateOf<MangaData?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Get manga details when the screen is displayed
    LaunchedEffect(mangaId) {
        isLoading = true
        // First try to get from memory
        var mangaData = mangaViewModel.getMangaById(mangaId)

        // If not found in memory, try to get from cache
        if (mangaData == null) {
            mangaData = mangaViewModel.getMangaByIdAsync(mangaId)
        }

        manga = mangaData
        isLoading = false
    }

    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text(text = manga?.title?.trim() ?: "Loading...") },
//                navigationIcon = {
//                    IconButton(onClick = {
//                        // Navigate back
//                        androidx.navigation.compose.rememberNavController().navigateUp()
//                    }) {
//                        Icon(
//                            imageVector = Icons.Default.ArrowBack,
//                            contentDescription = "Back"
//                        )
//                    }
//                }
//            )
//        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                // Show loading indicator
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (manga == null) {
                // Show error message if manga is not found
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Manga not found",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.Red
                    )
                }
            } else {
                // Show manga details
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // Manga image
                    GlideImage(
                        model = manga?.thumb,
                        contentDescription = manga?.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Title
                    Text(
                        text = manga?.title?.trim() ?: "",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    // Subtitle if available
                    if (!manga?.sub_title.isNullOrBlank()) {
                        Text(
                            text = manga?.sub_title?.trim() ?: "",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Status indicator
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        val statusColor = when (manga?.status?.lowercase()) {
                            "ongoing" -> Color.Green
                            "completed" -> Color.Blue
                            "hiatus" -> Color.Yellow
                            "discontinued" -> Color.Red
                            else -> Color.Gray
                        }
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(color = statusColor, shape = CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = manga?.status?.toTitleCase() ?: "",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Genres
                    Text(
                        text = "Genres",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = manga?.genres?.joinToString(", ") ?: "N/A",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Authors
                    Text(
                        text = "Authors",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = manga?.authors?.joinToString(", ") ?: "Unknown",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Summary
                    Text(
                        text = "Summary",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = manga?.summary?.trim() ?: "No summary available",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Justify
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Additional info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        InfoItem(title = "Type", value = manga?.type?.toTitleCase() ?: "N/A")
                        InfoItem(title = "Chapters", value = manga?.total_chapter?.toString() ?: "N/A")
                        InfoItem(title = "NSFW", value = if (manga?.nsfw == true) "Yes" else "No")
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Placeholder button - could be used for reading chapters, etc.
                    Button(
                        onClick = { /* Handle read action */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Start Reading")
                    }
                }
            }
        }
    }
}

@Composable
fun InfoItem(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}