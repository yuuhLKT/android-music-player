package com.example.musicplayer.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.example.musicplayer.domain.model.MusicModel
import com.example.musicplayer.presentation.ui.components.MusicItemCard
import com.example.musicplayer.presentation.ui.components.PlayButton
import com.example.musicplayer.presentation.ui.components.SearchBar
import com.example.musicplayer.presentation.ui.components.ShuffleButton
import com.example.musicplayer.presentation.viewmodel.MusicViewModel


@Composable
fun SongsScreen(musicViewModel: MusicViewModel) {
    var query by remember { mutableStateOf("") }
    val filteredMusicList by musicViewModel.filteredMusicList.collectAsState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    LazyColumn {
        item {
            SearchBar(
                query = remember { mutableStateOf(query) },
                onQueryChanged = { newQuery ->
                    query = newQuery
                    musicViewModel.filterMusic(newQuery)
                },
                onSearchDone = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                }
            )
        }
        item {
            ActionButtonsRow(filteredMusicList, musicViewModel)
        }
        items(filteredMusicList) { music ->
            MusicItemCard(
                context = context,
                music = music,
                query = query,
                onClick = {
                    focusManager.clearFocus()
                    musicViewModel.setCurrentPlaylist(filteredMusicList)
                    musicViewModel.playOrPauseMusic(music)
                }
            )
        }
    }
}

@Composable
fun ActionButtonsRow(musics: List<MusicModel>, musicViewModel: MusicViewModel) {
    Row(
        modifier = Modifier
            .padding(top = 12.dp, bottom = 12.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ShuffleButton(onClick = {
            musicViewModel.shuffleMusic(musics)
        })
        PlayButton(onClick = { musicViewModel.playFirstMusic(musics) })
    }
}