package com.example.musicplayer.presentation

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.musicplayer.presentation.ui.components.BottomNavigationBar
import com.example.musicplayer.presentation.ui.screens.HomeScreen
import com.example.musicplayer.presentation.ui.screens.MusicPlayerScreen
import com.example.musicplayer.presentation.ui.screens.PlaylistScreen
import com.example.musicplayer.presentation.ui.screens.PlaylistsScreen
import com.example.musicplayer.presentation.ui.screens.QueueListScreen
import com.example.musicplayer.presentation.ui.screens.SongsScreen
import com.example.musicplayer.presentation.viewmodel.MusicViewModel
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(
    context: Context,
    musicViewModel: MusicViewModel,
    navController: NavHostController
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0.0F,
        pageCount = { 3 }
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute != "musicPlayer/{id}/{musicName}/{artist}/{imageUrl}") {
                BottomNavigationBar(
                    onTabSelected = { index ->
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    selectedTabIndex = pagerState.currentPage
                )
            }
        }
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.padding(paddingValues),
        ) { page ->
            when (page) {
                0 -> HomeScreen(
                    musicViewModel = musicViewModel,
                    context = context,
                    navController = navController
                )
                1 -> SongsScreen(musicViewModel = musicViewModel)
                2 -> PlaylistsScreen(navController = navController)
            }
        }

        NavHost(navController = navController, startDestination = "app_navigation") {
            composable("app_navigation") {
                // This is the main navigation screen
            }
            composable("playlist_screen/{playlistId}") { backStackEntry ->
                val playlistId = backStackEntry.arguments?.getString("playlistId")?.toInt() ?: return@composable
                val playlist = musicViewModel.getPlaylistById(playlistId)
                PlaylistScreen(
                    context = context,
                    playlistModel = playlist,
                    onMusicClick = { id ->
                        val music = playlist.musics.find { it.id == id }
                        music?.let { musicViewModel.playOrPauseMusic(it) }
                    },
                    onShuffleClick = { musicViewModel.shuffleMusic(playlist.musics) },
                    onPlayClick = { musicViewModel.playFirstMusic(playlist.musics) },
                    musicViewModel = musicViewModel,
                    navController = navController
                )
            }
            composable("musicPlayer/{id}/{musicName}/{artist}/{imageUrl}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")?.toInt() ?: return@composable
                val music = musicViewModel.musicList.value.find { it.id == id } ?: return@composable
                MusicPlayerScreen(
                    context = context,
                    music = music,
                    musicViewModel = musicViewModel,
                    navController = navController
                )
            }
            composable("queue_list_screen") {
                QueueListScreen(
                    musicViewModel = musicViewModel,
                    navController = navController
                )
            }
        }
    }
}