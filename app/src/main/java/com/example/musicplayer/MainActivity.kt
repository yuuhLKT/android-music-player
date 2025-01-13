package com.example.musicplayer

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.musicplayer.domain.model.MusicModel
import com.example.musicplayer.presentation.AppNavigation
import com.example.musicplayer.presentation.theme.MusicPlayerTheme
import com.example.musicplayer.presentation.ui.components.MinimizedMusicPlayer
import com.example.musicplayer.presentation.ui.screens.MusicPlayerScreen
import com.example.musicplayer.presentation.ui.screens.QueueListScreen
import com.example.musicplayer.presentation.viewmodel.MusicViewModel
import com.example.musicplayer.presentation.viewmodel.MusicViewModelFactory
import com.example.musicplayer.util.PermissionUtil
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {

    private lateinit var musicViewModel: MusicViewModel

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = permissions.values.all { it }
            if (allGranted) {
                musicViewModel.loadMusicList()
            } else {
                Toast.makeText(this, "Permissions denied. Exiting app.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissions()
        setContent {
            MusicPlayerTheme {
                musicViewModel = viewModel(factory = MusicViewModelFactory(this))

                val isLoading by musicViewModel.isLoading.collectAsState()
                val navController = rememberNavController()
                val navigateToMusicPlayer by musicViewModel.navigateToMusicPlayer.collectAsState()
                val isMinimized by musicViewModel.isMinimized.collectAsState()
                val currentlyPlayingMusic by musicViewModel.currentlyPlayingMusic

                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(64.dp)
                                .align(Alignment.Center)
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            NavHost(navController = navController, startDestination = "home") {
                                composable("home") {
                                    AppNavigation(musicViewModel)
                                }
                                composable("musicPlayer/{musicId}/{musicName}/{artist}/{imageUrl}") { backStackEntry ->
                                    val musicId = backStackEntry.arguments?.getString("musicId")?.toInt() ?: 0
                                    val musicName = backStackEntry.arguments?.getString("musicName")?.let {
                                        URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
                                    } ?: ""
                                    val artist = backStackEntry.arguments?.getString("artist")?.let {
                                        URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
                                    } ?: ""
                                    val imageUrl = backStackEntry.arguments?.getString("imageUrl") ?: ""
                                    val filePath = ""

                                    MusicPlayerScreen(
                                        music = MusicModel(musicId, musicName, artist, 0L, imageUrl, filePath),
                                        navController = navController,
                                        musicViewModel = musicViewModel
                                    )
                                }
                                composable("queue_list_screen") {
                                    QueueListScreen(
                                        musicViewModel = musicViewModel,
                                        navController = navController
                                    )
                                }
                            }

                            if (isMinimized && currentlyPlayingMusic != null) {
                                MinimizedMusicPlayer(
                                    music = currentlyPlayingMusic,
                                    musicViewModel = musicViewModel,
                                    isPlaying = musicViewModel.isPlaying.collectAsState().value,
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .offset(y = (-80).dp)
                                )
                            }
                        }
                    }

                    navigateToMusicPlayer?.let { music ->
                        val encodedImageUrl = URLEncoder.encode(music.imageUrl, StandardCharsets.UTF_8.toString())
                        navController.navigate("musicPlayer/${music.id}/${music.musicName}/${music.artist}/$encodedImageUrl") {
                            popUpTo("musicPlayer") { inclusive = true }
                        }
                        musicViewModel._navigateToMusicPlayer.value = null
                    }
                }
            }
        }
    }

    private fun requestPermissions() {
        val permissions = PermissionUtil.getRequiredPermissions()
        requestPermissionsLauncher.launch(permissions)
    }
}