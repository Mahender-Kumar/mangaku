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
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.mangaku.core.model.MangaData
import com.example.mangaku.core.util.toTitleCase


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MangaScreen(onMangaClick: (MangaData) -> Unit) {
    val mangaViewModel: MangaViewModel = viewModel()

    // Observe manga data directly
    val mangaData = mangaViewModel.mangaData

    // If data is empty, show a loading indicator or text
    if (mangaData.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2), // 2 columns
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(mangaData) { manga ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp) // Fixed height for all cards
                        .padding(4.dp)
                        .clickable { onMangaClick(manga) }
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
                            modifier = Modifier.padding(8.dp)
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
                                    text = manga.status.toString().toTitleCase(),
                                    style = MaterialTheme.typography.labelSmall,
                                )

                            }

                        }


                    }
                }
            }
        }

    }

    // Call the function to fetch data if it's not already loaded
    LaunchedEffect(mangaData) {
        if (mangaData.isEmpty()) {
            mangaViewModel.fetchMangaData()
        }
    }

}
