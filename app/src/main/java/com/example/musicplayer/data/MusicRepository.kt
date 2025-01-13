package com.example.musicplayer.data

import android.content.ContentResolver
import android.content.Context
import android.provider.MediaStore
import com.example.musicplayer.domain.model.MusicModel

class MusicRepository(private val context: Context) {
    fun fetchMusic(): List<MusicModel> {
        val musicList = mutableListOf<MusicModel>()
        val contentResolver: ContentResolver = context.contentResolver

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.DATE_ADDED} DESC"

        contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn) ?: "Unknown"
                val artist = cursor.getString(artistColumn) ?: "Unknown Artist"
                val duration = cursor.getLong(durationColumn)
                val filePath = cursor.getString(dataColumn)
                val albumId = cursor.getLong(albumIdColumn)
                val imageUrl = getAlbumArtUri(albumId)

                musicList.add(
                    MusicModel(
                        id = id.toInt(),
                        musicName = name,
                        artist = artist,
                        duration = duration,
                        filePath = filePath,
                        imageUrl = imageUrl
                    )
                )
            }
        }

        return musicList
    }

    private fun getAlbumArtUri(albumId: Long): String {
        return "content://media/external/audio/albumart/$albumId"
    }
}