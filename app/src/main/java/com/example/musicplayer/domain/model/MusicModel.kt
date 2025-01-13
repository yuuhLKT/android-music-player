package com.example.musicplayer.domain.model

data class MusicModel(
    val id: Int,
    val musicName: String,
    val artist: String,
    val duration: Long,
    val imageUrl: String,
    val filePath: String,
    var playCount: Int = 0,
    var lastPlayed: Long = 0L,
    var isFavorite: Boolean = false
)