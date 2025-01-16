package com.example.musicplayer.presentation.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.musicplayer.presentation.ui.components.FavoritesButton
import com.example.musicplayer.presentation.ui.components.MostPlayedSection
import com.example.musicplayer.presentation.ui.components.RecentlyPlayedSection
import com.example.musicplayer.presentation.ui.components.ShuffleButton
import com.example.musicplayer.presentation.viewmodel.MusicViewModel

@Composable
fun HomeScreen(
    context: Context,
    musicViewModel: MusicViewModel,
    navController: NavHostController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ShuffleButton(onClick = {
                musicViewModel.shuffleMusic(musicViewModel.musicList.value)
            })
            FavoritesButton(onClick = {
                navController.navigate("playlist_screen/3")
            })
        }

        RecentlyPlayedSection(context = context, musicViewModel = musicViewModel)
        MostPlayedSection(context = context, musicViewModel = musicViewModel)
    }
}