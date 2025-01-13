package com.example.musicplayer.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.example.musicplayer.presentation.viewmodel.MusicViewModel

@Composable
fun RecentlyPlayedSection(musicViewModel: MusicViewModel) {
    val recentlyPlayed = musicViewModel.filteredMusicList.collectAsState().value
        .sortedByDescending { it.lastPlayed }
        .take(50)

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Recently Played",
            fontSize = 32.sp,
            modifier = Modifier.padding(top = 24.dp, bottom = 18.dp)
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(recentlyPlayed) { music ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(4.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .size(120.dp)
                            .clickable {
                                musicViewModel.setCurrentPlaylist(recentlyPlayed)
                                musicViewModel.playOrPauseMusic(music)
                            },
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(model = music.imageUrl),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = music.musicName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        modifier = Modifier.width(120.dp),
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = music.artist,
                        fontSize = 12.sp,
                        maxLines = 1,
                        modifier = Modifier.width(120.dp),
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}