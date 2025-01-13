package com.example.musicplayer.presentation.ui.components

import androidx.compose.runtime.Composable
import com.example.musicplayer.R

@Composable
fun ShuffleButton(onClick: () -> Unit) {
    IconButtonWithText(
        text = "Shuffle",
        icon = R.drawable.ic_shuffle,
        onClick = onClick
    )
}

@Composable
fun PlayButton(onClick: () -> Unit) {
    IconButtonWithText(
        text = "Play",
        icon = R.drawable.ic_play_arrow,
        onClick = onClick
    )
}

@Composable
fun FavoritesButton(onClick: () -> Unit) {
    IconButtonWithText(
        text = "Favorites",
        icon = R.drawable.ic_favorite_fill,
        onClick = onClick
    )
}