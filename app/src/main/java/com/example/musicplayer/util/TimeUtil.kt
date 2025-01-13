package com.example.musicplayer.util

object TimeUtil {
    fun formatDuration(duration: Long): String {
        val minutes = (duration / 1000) / 60
        val seconds = (duration/ 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}