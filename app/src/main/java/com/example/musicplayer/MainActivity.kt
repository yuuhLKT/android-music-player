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
import androidx.navigation.compose.rememberNavController
import com.example.musicplayer.presentation.AppNavigation
import com.example.musicplayer.presentation.theme.MusicPlayerTheme
import com.example.musicplayer.presentation.ui.components.MinimizedMusicPlayer
import com.example.musicplayer.presentation.viewmodel.MusicViewModel
import com.example.musicplayer.presentation.viewmodel.MusicViewModelFactory
import com.example.musicplayer.util.PermissionUtil
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
                        AppNavigation(
                            context = this@MainActivity,
                            musicViewModel = musicViewModel,
                            navController = navController
                        )

                        if (isMinimized && currentlyPlayingMusic != null) {
                            MinimizedMusicPlayer(
                                context = this@MainActivity,
                                music = currentlyPlayingMusic,
                                musicViewModel = musicViewModel,
                                isPlaying = musicViewModel.isPlaying.collectAsState().value,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .offset(y = (-80).dp)
                            )
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