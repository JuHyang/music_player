package com.juhyang.musicplayer.internal.presentation

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.juhyang.musicplayer.Song
import com.juhyang.musicplayer.internal.model.PlayerState
import com.juhyang.musicplayer.internal.model.PlayingState
import com.juhyang.musicplayer.internal.model.RepeatMode
import com.juhyang.musicplayer.internal.model.ShuffleMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


internal class MusicService: Service() {
    companion object {
        private var _playerState: MutableStateFlow<PlayerState> = MutableStateFlow(PlayerState())
        var playerState: StateFlow<PlayerState> = _playerState
        private var _playingState: MutableStateFlow<PlayingState> = MutableStateFlow(PlayingState())
        var playingState: StateFlow<PlayingState> = _playingState
    }

    inner class MusicServiceBinder: Binder() {
        fun getService(): MusicService {
            return this@MusicService
        }
    }

    private val mediaPlayer: MediaPlayer = MediaPlayer()
    private val mBinder: MusicServiceBinder = MusicServiceBinder()
    override fun onBind(intent: Intent?): IBinder {
        return mBinder
    }

    override fun onCreate() {
        super.onCreate()
        startForegroundService()

        mediaPlayer.setOnCompletionListener {
            playNextSong()
        }
    }

    private fun startForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val mChannel = NotificationChannel( // 알림 채널 생성 (필수)
                "CHANNEL_ID",
                "CHANNEL_NAME",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(mChannel)
        }

        val notification: Notification = NotificationCompat.Builder(this, "CHANNEL_ID")
//            .setSmallIcon(R.drawable.ic_play) // 알림 아이콘
            .setContentTitle("뮤직 플레이어 앱") // 알림 제목 설정
            .setContentText("앱이 실행중입니다.") // 알림 내용 설정
            .build()
        startForeground(222, notification) // 인수로 알림 ID 식별자와 알림 지정, 그런데 이 부분에서 오류나서 꺼짐, 주석 처리 할 것
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundService()
        return START_STICKY
    }

    private var playList: MutableList<Song> = mutableListOf()
    private var currentPlayingSongIndex = 0

    override fun onDestroy() {
        mediaPlayer.stop()
        super.onDestroy()
    }

    fun play(songs: List<Song>) {
        playList.clear()
        playList.addAll(songs)
        currentPlayingSongIndex = 0
        play(playList[currentPlayingSongIndex])
    }

    fun play(index: Int) {
        currentPlayingSongIndex = index
        play(playList[currentPlayingSongIndex])
    }

    private fun play(song: Song) {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(song.filePath)
        mediaPlayer.prepare()
        mediaPlayer.start()

        startUpdatingPosition()

        _playingState.value = PlayingState(
            currentSong = song,
            isPlaying = true,
            currentPosition = 0,
            totalDuration = mediaPlayer.duration
        )
    }

    fun playNextSong() {
        val nextSong = getNextSong()
        if (nextSong == null) {
            stop()
            return
        }

        play(nextSong)
    }

    private fun getNextSong(): Song? {
        val repeatMode = _playerState.value.repeatMode
        if (repeatMode == RepeatMode.ONE) {
            return _playingState.value.currentSong
        }

        val shuffleMode = _playerState.value.shuffleMode
        if (shuffleMode == ShuffleMode.ON) {
            return playList.random()
        }

        currentPlayingSongIndex += 1
        if (currentPlayingSongIndex >= playList.size) {
            if (repeatMode == RepeatMode.ALL) {
                currentPlayingSongIndex = 0
            } else {
                return null
            }
        }

        return playList[currentPlayingSongIndex]
    }

    fun playPreviousSong() {
        if (_playingState.value.currentPosition > 2000) {
            seekTo(0)
            return
        }
        val previousSong = getPreviousSong()
        play(previousSong)
    }

    private fun getPreviousSong(): Song {
        val previousIndex = currentPlayingSongIndex - 1
        if (previousIndex < 0) {
            currentPlayingSongIndex = 0
        }

        return playList[currentPlayingSongIndex]
    }

    fun stop() {
        mediaPlayer.stop()
        _playingState.value = _playingState.value.copy(isPlaying = false)
        stopUpdatingPosition()
    }

    fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    fun resume() {
        mediaPlayer.start()
        startUpdatingPosition()

        _playingState.value = _playingState.value.copy(isPlaying = true)
    }

    fun pause() {
        mediaPlayer.pause()

        _playingState.value = _playingState.value.copy(isPlaying = false)
    }

    fun seekTo(position: Int) {
        mediaPlayer.seekTo(position)
    }

    fun changeRepeatMode() {
        val newPlayerState = _playerState.value.copy(repeatMode = _playerState.value.repeatMode.next())
        _playerState.value = newPlayerState
    }

    fun changeShuffleMode() {
        val newPlayerState = _playerState.value.copy(shuffleMode = _playerState.value.shuffleMode.toggle())
        _playerState.value = newPlayerState
    }

    fun getPlayList(): List<Song> {
        return playList
    }

    private var updateJob: Job? = null
    private fun startUpdatingPosition() {
        stopUpdatingPosition()
        updateJob = CoroutineScope(Dispatchers.IO).launch {
            while (_playingState.value.isPlaying) {
                _playingState.value = _playingState.value.copy(
                    currentPosition = mediaPlayer.currentPosition
                )
                delay(1000) // 1초 간격으로 업데이트
            }
        }
    }

    private fun stopUpdatingPosition() {
        updateJob?.cancel()
    }
}


