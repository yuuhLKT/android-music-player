package com.example.musicplayer.presentation.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.musicplayer.R
import com.example.musicplayer.domain.model.PlaylistModel
import com.example.musicplayer.presentation.ui.components.MusicItemCard
import com.example.musicplayer.presentation.ui.components.PlayButton
import com.example.musicplayer.presentation.ui.components.SearchBar
import com.example.musicplayer.presentation.ui.components.ShuffleButton
import com.example.musicplayer.presentation.viewmodel.MusicViewModel

@Composable
fun PlaylistScreen(
    context: Context,
    playlistModel: PlaylistModel,
    onMusicClick: (Int) -> Unit,
    onShuffleClick: () -> Unit,
    onPlayClick: () -> Unit,
    musicViewModel: MusicViewModel,
    navController: NavHostController
) {
    var query by remember { mutableStateOf("") }
    val filteredMusics by remember(query) {
        derivedStateOf {
            if (query.isEmpty()) {
                playlistModel.musics
            } else {
                playlistModel.musics.filter {
                    it.musicName.contains(query, ignoreCase = true) || it.artist.contains(query, ignoreCase = true)
                }
            }
        }
    }

    val listState = rememberLazyListState()
    val isScrolled by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1C1A21))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = playlistModel.playlistName,
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "${playlistModel.songCount} songs",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        if (!isScrolled) {
            Spacer(modifier = Modifier.height(16.dp))
            SearchBar(
                query = remember { mutableStateOf(query) },
                onQueryChanged = { newQuery -> query = newQuery },
                onSearchDone = {}
            )
            Spacer(modifier = Modifier.height(16.dp))
            ActionButtonsRowPlaylist(onShuffleClick, onPlayClick)
        }
        LazyColumn(state = listState) {
            items(filteredMusics) { music ->
                MusicItemCard(
                    context = context,
                    music = music,
                    query = query,
                    onClick = { onMusicClick(music.id) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun ActionButtonsRowPlaylist(onShuffleClick: () -> Unit, onPlayClick: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(top = 12.dp, bottom = 12.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ShuffleButton(onClick = onShuffleClick)
        PlayButton(onClick = onPlayClick)
    }
}