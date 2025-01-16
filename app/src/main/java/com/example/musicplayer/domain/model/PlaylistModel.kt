package com.example.musicplayer.domain.model

data class PlaylistModel(
    var playlistName: String,
    var songCount: Int,
    var imageResId: Int,
    var playlistId: Int,
    var musics: List<MusicModel>
)