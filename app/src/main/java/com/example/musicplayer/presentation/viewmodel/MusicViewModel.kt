package com.example.musicplayer.presentation.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.example.musicplayer.data.MusicRepository
import com.example.musicplayer.domain.model.MusicModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.net.URLEncoder

class MusicViewModel(context: Context) : ViewModel() {
    private val musicRepository = MusicRepository(context)
    private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    private val _musicList = MutableStateFlow<List<MusicModel>>(emptyList())
    val musicList: StateFlow<List<MusicModel>> = _musicList

    private val _filteredMusicList = MutableStateFlow<List<MusicModel>>(emptyList())
    val filteredMusicList: StateFlow<List<MusicModel>> = _filteredMusicList

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _currentDuration = MutableStateFlow(0L)
    val currentDuration: StateFlow<Long> = _currentDuration

    private val _totalDuration = MutableStateFlow(0L)
    val totalDuration: StateFlow<Long> = _totalDuration

    private var mediaPlayer: MediaPlayer? = null
    private val _currentlyPlayingMusic = mutableStateOf<MusicModel?>(null)
    val currentlyPlayingMusic: State<MusicModel?> = _currentlyPlayingMusic

    private val _currentPlaylist = MutableStateFlow<List<MusicModel>>(emptyList())
    val currentPlaylist: StateFlow<List<MusicModel>> = _currentPlaylist

    private val _isMinimized = MutableStateFlow(false)
    var isMinimized: StateFlow<Boolean> = _isMinimized

    val _navigateToMusicPlayer = MutableStateFlow<MusicModel?>(null)
    val navigateToMusicPlayer: StateFlow<MusicModel?> = _navigateToMusicPlayer

    private val _queueList = MutableStateFlow<List<MusicModel>>(emptyList())
    val queueList: StateFlow<List<MusicModel>> = _queueList

    private val _isShuffleEnabled = MutableStateFlow(false)
    val isShuffleEnabled: StateFlow<Boolean> = _isShuffleEnabled

    private var progressUpdateJob: Job? = null

    init {
        loadMusicList()
        loadSavedState()
    }

    fun setCurrentPlaylist(playlist: List<MusicModel>) {
        _currentPlaylist.value = playlist
    }

    fun minimizePlayer() {
        _isMinimized.value = true
    }

    fun maximizePlayer() {
        _isMinimized.value = false
        _currentlyPlayingMusic.value?.let {
            _navigateToMusicPlayer.value = it
        }
    }

    fun shuffleMusic() {
        if (_musicList.value.isNotEmpty()) {
            _isShuffleEnabled.value = true

            val currentMusic = _currentlyPlayingMusic.value
            val availableMusic = _musicList.value.filter { it != currentMusic }

            if (availableMusic.isNotEmpty()) {
                val randomMusic = availableMusic.random()
                val remainingMusic = _musicList.value
                    .filter { it != currentMusic && it != randomMusic }
                    .shuffled()

                val shuffledQueue = listOf(randomMusic) + remainingMusic

                _queueList.value = shuffledQueue
                _currentPlaylist.value = shuffledQueue
                playOrPauseMusic(randomMusic)
            }

            saveShuffleState()
        }
    }

    private fun doShuffle(currentMusic: MusicModel?) {
        val remainingMusic = _musicList.value.filter { it != currentMusic }.shuffled()
        val shuffledQueue = if (currentMusic != null) {
            listOf(currentMusic) + remainingMusic
        } else {
            remainingMusic
        }

        _queueList.value = shuffledQueue
        _currentPlaylist.value = shuffledQueue

        updateQueueList()
    }

    fun playFirstMusic() {
        val firstMusic = _musicList.value[0]
        playOrPauseMusic(firstMusic)
    }

    fun toggleFavorite(music: MusicModel) {
        val updatedMusic = music.copy(isFavorite = !music.isFavorite)
        val updatedList = _musicList.value.map {
            if (it.id == music.id) updatedMusic else it
        }
        _musicList.value = updatedList
        saveFavoriteState(updatedMusic)
    }

    private fun encodeForNavigation(str: String): String {
        return URLEncoder.encode(str, "UTF-8")
    }

    fun seekTo(position: Long) {
        mediaPlayer?.seekTo(position.toInt())
    }

    fun replayTenSeconds() {
        mediaPlayer?.let { player ->
            val newPosition = (player.currentPosition - 10000).coerceAtLeast(0)
            player.seekTo(newPosition)
        }
    }

    fun forwardTenSeconds() {
        mediaPlayer?.let { player ->
            val newPosition = (player.currentPosition + 10000).coerceAtMost(player.duration)
            player.seekTo(newPosition)
        }
    }

    fun playNextSong() {
        _currentlyPlayingMusic.value?.let { currentMusic ->
            val currentIndex = _currentPlaylist.value.indexOf(currentMusic)
            val nextIndex = (currentIndex + 1) % _currentPlaylist.value.size
            val nextSong = _currentPlaylist.value[nextIndex]
            playOrPauseMusic(nextSong, keepMinimized = true)
        }
    }

    fun playPreviousSong() {
        mediaPlayer?.let { player ->
            if (player.currentPosition >= 15000) {
                player.seekTo(0)
                player.start()
                _isPlaying.value = true
                startProgressUpdate()
            } else {
                _currentlyPlayingMusic.value?.let { currentMusic ->
                    val currentIndex = _currentPlaylist.value.indexOf(currentMusic)
                    val previousIndex = if (currentIndex > 0) currentIndex - 1 else _currentPlaylist.value.size - 1
                    val previousSong = _currentPlaylist.value[previousIndex]
                    playOrPauseMusic(previousSong, keepMinimized = true)
                }
            }
        }
    }

    private fun startProgressUpdate() {
        progressUpdateJob?.cancel()
        progressUpdateJob = viewModelScope.launch {
            while (true) {
                mediaPlayer?.let { player ->
                    if (player.isPlaying) {
                        _currentDuration.value = player.currentPosition.toLong()
                        _totalDuration.value = player.duration.toLong()
                    }
                }
                delay(100)
            }
        }
    }

    fun playMusic() {
        mediaPlayer?.let { player ->
            player.start()
            _isPlaying.value = true
            startProgressUpdate()
            savePlaybackState(true)
        }
    }

    fun pauseMusic() {
        mediaPlayer?.let { player ->
            player.pause()
            _isPlaying.value = false
            progressUpdateJob?.cancel()
            savePlaybackState(false)
        }
    }

    private fun updateQueueList() {
        val currentMusic = _currentlyPlayingMusic.value
        val currentPlaylist = if (_isShuffleEnabled.value) {
            _currentPlaylist.value
        } else {
            _musicList.value
        }

        val currentIndex = currentPlaylist.indexOf(currentMusic)
        if (currentIndex != -1) {
            val nextSongs = currentPlaylist.drop(currentIndex)
            _queueList.value = nextSongs
        }
    }

    fun playOrPauseMusic(music: MusicModel, keepMinimized: Boolean = false) {
        if (!keepMinimized) {
            _isMinimized.value = false
        }
        if (_currentlyPlayingMusic.value == music) {
            mediaPlayer?.let { player ->
                if (player.isPlaying) {
                    pauseMusic()
                } else {
                    playMusic()
                }
            }
        } else {
            val musicFile = File(music.filePath)
            if (!musicFile.exists()) {
                return
            }

            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(music.filePath)
                setOnPreparedListener {
                    it.start()
                    _currentlyPlayingMusic.value = music
                    _isPlaying.value = true
                    music.playCount++
                    music.lastPlayed = System.currentTimeMillis()
                    saveMusicState(music)
                    savePlaybackState(true)
                    startProgressUpdate()
                    updateQueueList()
                    if (!_isMinimized.value) {
                        val encodedMusic = music.copy(
                            musicName = encodeForNavigation(music.musicName),
                            artist = encodeForNavigation(music.artist),
                            imageUrl = encodeForNavigation(music.imageUrl)
                        )
                        _navigateToMusicPlayer.value = encodedMusic
                    }
                }
                setOnCompletionListener {
                    _isPlaying.value = false
                    progressUpdateJob?.cancel()
                    playNextSong()
                }
                setOnErrorListener { _, _, _ -> true }
                prepareAsync()
            }
        }
    }

    private fun saveMusicState(music: MusicModel) {
        with(sharedPreferences.edit()) {
            putInt("music_play_count_${music.id}", music.playCount)
            putLong("music_last_played_${music.id}", music.lastPlayed)
            apply()
        }
    }

    private fun savePlaybackState(isPlaying: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean("is_playing", isPlaying)
            apply()
        }
    }

    private fun saveShuffleState() {
        with(sharedPreferences.edit()) {
            putBoolean("is_shuffle_enabled", _isShuffleEnabled.value)
            apply()
        }
    }

    private fun saveFavoriteState(music: MusicModel) {
        with(sharedPreferences.edit()) {
            putBoolean("music_favorite_${music.id}", music.isFavorite)
            apply()
        }
    }

    private fun loadSavedState() {
        val musicId = sharedPreferences.getInt("current_music_id", -1)
        if (musicId != -1) {
            val music = _musicList.value.find { it.id == musicId }
            if (music != null) {
                _currentlyPlayingMusic.value = music
                _musicList.value.forEach { loadFavoriteState(it) }
                music.lastPlayed = sharedPreferences.getLong("music_last_played_${music.id}", 0L)
                music.playCount = sharedPreferences.getInt("music_play_count_${music.id}", 0)
            }
        }
        _isShuffleEnabled.value = sharedPreferences.getBoolean("is_shuffle_enabled", false)
        if (_isShuffleEnabled.value) {
            val currentMusic = _currentlyPlayingMusic.value
            if (currentMusic != null) {
                val remainingMusic = _musicList.value.filter { it != currentMusic }.shuffled()
                _currentPlaylist.value = listOf(currentMusic) + remainingMusic
                _queueList.value = _currentPlaylist.value
            }
        }
    }

    fun loadMusicList() {
        viewModelScope.launch {
            _isLoading.value = true
            val music = musicRepository.fetchMusic()
            music.forEach { loadFavoriteState(it) }
            _musicList.value = music
            _filteredMusicList.value = music
            loadMusicPlayCounts()
            _isLoading.value = false
        }
    }

    private fun loadFavoriteState(music: MusicModel) {
        music.isFavorite = sharedPreferences.getBoolean("music_favorite_${music.id}", false)
    }

    fun filterMusic(query: String) {
        if (query.isEmpty()) {
            _filteredMusicList.value = _musicList.value
        } else {
            _filteredMusicList.value = _musicList.value.filter {
                it.musicName.contains(query, ignoreCase = true) || it.artist.contains(query, ignoreCase = true)
            }
        }
    }

    private fun loadMusicPlayCounts() {
        _musicList.value.forEach { music ->
            music.playCount = sharedPreferences.getInt("music_play_count_${music.id}", 0)
            music.lastPlayed = sharedPreferences.getLong("music_last_played_${music.id}", 0L)
        }
    }

    override fun onCleared() {
        super.onCleared()
        progressUpdateJob?.cancel()
        mediaPlayer?.release()
    }
}