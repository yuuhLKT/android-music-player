package com.example.musicplayer.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.example.musicplayer.MainActivity
import com.example.musicplayer.R
import com.example.musicplayer.domain.model.MusicModel
import com.example.musicplayer.util.ImageUtil
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MusicService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var mediaSession: MediaSessionCompat
    private val binder = MusicBinder()
    private var currentlyPlayingMusic: MusicModel? = null
    private var isPlaying: Boolean = false

    companion object {
        const val CHANNEL_ID = "MusicPlayerChannel"
        const val NOTIFICATION_ID = 1
    }

    inner class MusicBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        mediaSession = MediaSessionCompat(this, "MusicPlayer")
    }

    override fun onBind(intent: Intent?): IBinder = binder

    @SuppressLint("ObsoleteSdkInt")
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Music Player",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Music player controls"
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun updateNotification(music: MusicModel, isPlaying: Boolean) {
        this.currentlyPlayingMusic = music
        this.isPlaying = isPlaying

        val intent = Intent(this, MainActivity::class.java).apply {
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
            putExtra("musicId", music.id)
            putExtra("musicName", URLEncoder.encode(music.musicName, StandardCharsets.UTF_8.toString()))
            putExtra("artist", URLEncoder.encode(music.artist, StandardCharsets.UTF_8.toString()))
            putExtra("imageUrl", URLEncoder.encode(music.imageUrl, StandardCharsets.UTF_8.toString()))
        }

        val contentIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val playPauseIcon = if (isPlaying) R.drawable.ic_pause_music else R.drawable.ic_play_arrow
        val prevIntent = PendingIntent.getBroadcast(
            this, 1,
            Intent("PREVIOUS_ACTION"),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val playIntent = PendingIntent.getBroadcast(
            this, 2,
            Intent("PLAY_PAUSE_ACTION"),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val nextIntent = PendingIntent.getBroadcast(
            this, 3,
            Intent("NEXT_ACTION"),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentIntent(contentIntent)
            .setContentTitle(music.musicName)
            .setContentText(music.artist)
            .setSmallIcon(R.drawable.ic_play_arrow)
            .setLargeIcon(ImageUtil.getImgArt(this, music.filePath))
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(mediaSession.sessionToken)
                .setShowActionsInCompactView(0, 1, 2))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.ic_prev_music, "Previous", prevIntent)
            .addAction(playPauseIcon, "Play/Pause", playIntent)
            .addAction(R.drawable.ic_next_music, "Next", nextIntent)

        if (!isPlaying) {
            notificationBuilder.setOngoing(false)
        } else {
            notificationBuilder.setOngoing(true)
        }

        val notification = notificationBuilder.build()
        startForeground(NOTIFICATION_ID, notification)

        if (isPlaying) {
            startForeground(NOTIFICATION_ID, notification)
        } else {
            stopForeground(false)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.notify(NOTIFICATION_ID, notification)
        }
    }

    fun setMediaPlayer(player: MediaPlayer) {
        mediaPlayer = player
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaSession.release()
    }
}