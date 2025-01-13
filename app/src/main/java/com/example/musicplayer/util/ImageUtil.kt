package com.example.musicplayer.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import com.example.musicplayer.R

object ImageUtil {
    fun getImgArt(context: Context, path: String): Bitmap {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(path)
        val imgArt = retriever.embeddedPicture
        return if (imgArt != null) {
            BitmapFactory.decodeByteArray(imgArt, 0, imgArt.size)
        } else {
            BitmapFactory.decodeResource(context.resources, R.drawable.ic_music_player)
        }
    }
}