package com.example.musicplayer.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.musicplayer.R
import com.example.musicplayer.presentation.ui.components.MusicItemCard
import com.example.musicplayer.presentation.viewmodel.MusicViewModel

@Composable
fun QueueListScreen(musicViewModel: MusicViewModel, navController: NavHostController) {
    val queueList by musicViewModel.queueList.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .background(Color(0xFF1C1A21))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    onClick = {
                        navController.popBackStack()
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_back),
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Music Queue (${queueList.size})",
                    fontSize = 32.sp,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
            LazyColumn {
                items(queueList) { music ->
                    MusicItemCard(
                        music = music,
                        query = "",
                        onClick = {
                            musicViewModel.playOrPauseMusic(music)
                        }
                    )
                }
            }
        }
    }
}