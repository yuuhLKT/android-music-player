package com.example.musicplayer.presentation

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.musicplayer.presentation.ui.components.BottomNavigationBar
import com.example.musicplayer.presentation.ui.screens.HomeScreen
import com.example.musicplayer.presentation.ui.screens.PlaylistsScreen
import com.example.musicplayer.presentation.ui.screens.SongsScreen
import com.example.musicplayer.presentation.viewmodel.MusicViewModel
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(context: Context, musicViewModel: MusicViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0.0F,
        pageCount = { 3 }
    )

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                onTabSelected = { index ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
                selectedTabIndex = pagerState.currentPage
            )
        }
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.padding(paddingValues),
        ) { page ->
            when (page) {
                0 -> HomeScreen(
                    musicViewModel = musicViewModel,
                    context = context
                )
                1 -> SongsScreen(musicViewModel = musicViewModel)
                2 -> PlaylistsScreen()
            }
        }
    }
}