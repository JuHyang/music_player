package com.juhyang.musicplayer.internal.presentation

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.session.PlaybackState
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import com.juhyang.musicplayer.R
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


internal class MusicService : Service() {
    companion object {
        private var _playerState: MutableStateFlow<PlayerState> = MutableStateFlow(PlayerState())
        var playerState: StateFlow<PlayerState> = _playerState
        private var _playingState: MutableStateFlow<PlayingState> = MutableStateFlow(PlayingState())
        var playingState: StateFlow<PlayingState> = _playingState

        private const val NOTIFICATION_ID = 100

        private const val ACTION_PLAY = "ACTION_PLAY"
        private const val ACTION_PAUSE = "ACTION_PAUSE"
    }

    inner class MusicServiceBinder : Binder() {
        fun getService(): MusicService {
            return this@MusicService
        }
    }

    private val mediaPlayer: MediaPlayer = MediaPlayer()
    private val mediaSession: MediaSessionCompat by lazy { MediaSessionCompat(this, "MusicService") }
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

        mediaSession.setCallback(object : MediaSessionCompat.Callback() {
            override fun onPlay() {
                super.onPlay()
                resume()
                updatePlaybackState(PlaybackState.STATE_PLAYING)
            }

            override fun onPause() {
                super.onPause()
                pause()
                updatePlaybackState(PlaybackState.STATE_PAUSED)
            }

            override fun onSkipToNext() {
                super.onSkipToNext()
                playNextSong()
            }

            override fun onSkipToPrevious() {
                super.onSkipToPrevious()
                playPreviousSong()
            }

            override fun onStop() {
                super.onStop()
                stop()
                updatePlaybackState(PlaybackState.STATE_STOPPED)
            }
        })
        mediaSession.isActive = true
    }

    private fun updatePlaybackState(state: Int) {
        mediaSession.setPlaybackState(
            PlaybackStateCompat.Builder()
                .setActions(
                    PlaybackStateCompat.ACTION_PLAY or
                            PlaybackStateCompat.ACTION_PAUSE or
                            PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                            PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                            PlaybackStateCompat.ACTION_STOP
                )
                .setState(state, mediaPlayer.currentPosition.toLong(), 1f)
                .build()
        )
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

        startForeground(NOTIFICATION_ID, getNotification())
    }

    private fun getNotification(): Notification {
        val playIntent = Intent(this, MusicService::class.java).apply {
            action = MusicService.ACTION_PLAY  // 정의된 액션
        }

        val playPendingIntent: PendingIntent = PendingIntent.getService(
            this,
            0,
            playIntent,
            PendingIntent.FLAG_MUTABLE
        )

        val pauseIntent = Intent(this, MusicService::class.java).apply {
            action = MusicService.ACTION_PAUSE  // 정의된 액션
        }

        val pausePendingIntent: PendingIntent = PendingIntent.getService(
            this,
            0,
            pauseIntent,
            PendingIntent.FLAG_MUTABLE
        )

        return NotificationCompat.Builder(this, "CHANNEL_ID")
            .setContentTitle("Music Player")
            .setContentText("Now Playing")
            .setSmallIcon(R.drawable.play_arrow)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken)
            )
            .addAction(R.drawable.play_arrow, "Play", playPendingIntent)  // 재생 버튼
            .addAction(R.drawable.pause, "Pause", pausePendingIntent)  // 일시정지 버튼
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // 잠금 화면에 표시
            .build()
    }

    private fun updateNotification(songTitle: String, artist: String, albumArt: String) {
        // 메타데이터 설정
        val mediaMetadata = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, songTitle)
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, albumArt)
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, mediaPlayer.duration.toLong())
            .build()

        mediaSession.setMetadata(mediaMetadata)

        // 알림 생성
        val notification = getNotification()

        // 기존 알림을 갱신
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundService()

        val action = intent?.action
        when (action) {
            ACTION_PLAY -> {
                // 음악 재생 처리
                resume()
            }
            ACTION_PAUSE -> {
                // 음악 일시정지 처리
                pause()
            }
        }

        return START_STICKY
    }

    private var playList: MutableList<Song> = mutableListOf()
    private var currentPlayingSongIndex = 0

    override fun onDestroy() {
        mediaPlayer.stop()
        mediaPlayer.release()
        mediaSession.release()
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

        updateNotification(song.title, song.artistName, song.albumCoverUri.toString())
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


