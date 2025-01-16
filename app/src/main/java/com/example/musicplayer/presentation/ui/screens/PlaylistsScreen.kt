package com.example.musicplayer.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.musicplayer.presentation.ui.components.PlaylistItem
import com.example.musicplayer.presentation.viewmodel.MusicViewModel
import com.example.musicplayer.presentation.viewmodel.MusicViewModelFactory

@Composable
fun PlaylistsScreen(navController: NavHostController) {
    val context = LocalContext.current
    val musicViewModel: MusicViewModel = viewModel(factory = MusicViewModelFactory(context))

    val recentlyPlayed = musicViewModel.getRecentlyPlayedPlaylist().collectAsState().value
    val mostPlayed = musicViewModel.getMostPlayedPlaylist().collectAsState().value
    val favorites = musicViewModel.getFavoritesPlaylist().collectAsState().value

    val playlists = listOf(recentlyPlayed, mostPlayed, favorites)
    val numberOfPlaylists = playlists.size

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Playlists ($numberOfPlaylists)",
            fontSize = 24.sp,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )
        playlists.forEach { playlist ->
            PlaylistItem(playlistModel = playlist) { playlistId ->
                navController.navigate("playlist_screen/${playlist.playlistId}")
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}