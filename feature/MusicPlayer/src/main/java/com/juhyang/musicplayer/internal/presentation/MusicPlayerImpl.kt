package com.juhyang.musicplayer.internal.presentation

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import com.juhyang.musicplayer.MusicPlayer
import com.juhyang.musicplayer.Song
import com.juhyang.musicplayer.internal.model.PlayerState
import com.juhyang.musicplayer.internal.model.PlayingState
import com.juhyang.permission.PermissionChecker
import com.juhyang.permission.model.NotificationPermission
import kotlinx.coroutines.flow.StateFlow


internal class MusicPlayerImpl private constructor() : MusicPlayer {
    companion object {
        val instance by lazy { MusicPlayerImpl() }
    }

    private var musicService: MusicService? = null
    private val serviceConnection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            musicService = (service as MusicService.MusicServiceBinder).getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicService = null
        }
    }

    override fun onStart(activity: Activity) {
        PermissionChecker.instance.requestPermissionIfNeeded(activity, NotificationPermission(), isRequired = false)
    }

    override fun onResume(activity: Activity) {
        if (musicService == null) {
            val musicServiceIntent = Intent(activity, MusicService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                activity.startForegroundService(musicServiceIntent)
            } else {
                activity.startService(musicServiceIntent)
            }
        }

        val intent = Intent(activity, MusicService::class.java)
        activity.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onPause(activity: Activity) {
        val musicPlayer = musicService ?: return

        if (!musicPlayer.isPlaying()) {
            musicPlayer.stopSelf()
        }

        activity.unbindService(serviceConnection)
        this.musicService = null
    }

    override fun play(songs: List<Song>) {
        musicService?.play(songs)
    }

    override fun play(index: Int) {
        musicService?.play(index)
    }

    override fun resume() {
        musicService?.resume()
    }

    override fun pause() {
        musicService?.pause()
    }

    override fun stop() {
        musicService?.stop()
    }

    override fun changeRepeatMode() {
        musicService?.changeRepeatMode()
    }

    override fun changeShuffleMode() {
        musicService?.changeShuffleMode()
    }

    override fun getPlayingState(): StateFlow<PlayingState> {
        return MusicService.playingState
    }

    override fun getPlayerState(): StateFlow<PlayerState> {
        return MusicService.playerState
    }

    override fun skipToNext() {
        musicService?.playNextSong()
    }

    override fun skipToPrevious() {
        musicService?.playPreviousSong()
    }

    override fun seekTo(position: Int) {
        musicService?.seekTo(position)
    }

    override fun isPlaying(): Boolean {
        return musicService?.isPlaying() ?: false
    }

    override fun getPlaylist(): List<Song> {
        return musicService?.getPlayList() ?: emptyList()
    }

    override fun addPlayList(song: Song) {
        musicService?.addPlayList(song)
    }
}
