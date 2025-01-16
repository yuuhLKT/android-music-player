package com.example.musicplayer.presentation.ui.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.musicplayer.R
import com.example.musicplayer.domain.model.MusicModel
import com.example.musicplayer.presentation.viewmodel.MusicViewModel
import com.example.musicplayer.util.ImageUtil
import com.example.musicplayer.util.TimeUtil

@Composable
fun MusicPlayerScreen(
    context: Context,
    music: MusicModel,
    navController: NavHostController,
    musicViewModel: MusicViewModel
) {
    val currentDuration by musicViewModel.currentDuration.collectAsState()
    val totalDuration by musicViewModel.totalDuration.collectAsState()
    val isPlaying by musicViewModel.isPlaying.collectAsState()
    val updatedMusic = musicViewModel.musicList.collectAsState().value.find { it.id == music.id } ?: music

    val imageBitmap = remember { ImageUtil.getImgArt(context, updatedMusic.filePath) }
    val imagePainter = remember { BitmapPainter(imageBitmap.asImageBitmap()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF49444E))
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (isPlaying) {
                        musicViewModel.minimizePlayer()
                    }
                    navController.navigate("app_navigation")
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_minimize_music),
                    contentDescription = "Minimize Music",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Image(
            painter = imagePainter,
            contentDescription = "Album Art",
            modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Text(
                text = updatedMusic.musicName,
                fontSize = 16.sp,
                maxLines = 1,
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier.basicMarquee()
            )
            Text(
                text = updatedMusic.artist,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Slider(
                value = if (totalDuration > 0) currentDuration.toFloat() / totalDuration.toFloat() else 0f,
                onValueChange = { newValue ->
                    musicViewModel.seekTo((newValue * totalDuration).toLong())
                },
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.White,
                    inactiveTrackColor = Color.Gray
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = TimeUtil.formatDuration(currentDuration),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = TimeUtil.formatDuration(totalDuration),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { musicViewModel.replayTenSeconds() }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_replay_10_music),
                    contentDescription = "Replay 10s",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
            IconButton(
                onClick = { musicViewModel.playPreviousSong() }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_prev_music),
                    contentDescription = "Previous",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
            if (isPlaying) {
                IconButton(
                    onClick = { musicViewModel.pauseMusic() },
                    modifier = Modifier.size(72.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_pause_music),
                        contentDescription = "Pause",
                        tint = Color.White,
                        modifier = Modifier.size(56.dp)
                    )
                }
            } else {
                IconButton(
                    onClick = { musicViewModel.playMusic() },
                    modifier = Modifier.size(72.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_play_arrow),
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier.size(56.dp)
                    )
                }
            }
            IconButton(
                onClick = { musicViewModel.playNextSong() }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_next_music),
                    contentDescription = "Next",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
            IconButton(
                onClick = { musicViewModel.forwardTenSeconds() }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_forward_10_music),
                    contentDescription = "Forward 10s",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* TODO: Add to playlist */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_playlist_add_music),
                    contentDescription = "Add to Playlist",
                    tint = Color.Gray,
                    modifier = Modifier.size(40.dp)
                )
            }
            IconButton(onClick = { musicViewModel.toggleFavorite(updatedMusic) }) {
                Icon(
                    painter = painterResource(id = if (updatedMusic.isFavorite) R.drawable.ic_favorite_fill else R.drawable.ic_favorite),
                    contentDescription = if (updatedMusic.isFavorite) "Remove from Favorites" else "Add to Favorites",
                    tint = if (updatedMusic.isFavorite) Color.Red else Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
            IconButton(onClick = { navController.navigate("queue_list_screen") }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_queue_music),
                    contentDescription = "Queue",
                    tint = Color.Gray,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}