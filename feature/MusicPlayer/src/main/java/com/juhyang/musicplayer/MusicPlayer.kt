package com.juhyang.musicplayer

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import com.juhyang.musicplayer.internal.MusicService
import com.juhyang.musicplayer.internal.model.PlayerState
import com.juhyang.musicplayer.internal.model.PlayingState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf


public interface MusicPlayer {
    companion object {
        val instance: MusicPlayer by lazy { MusicPlayerImpl.instance }
    }

    fun onResume(activity: Activity)
    fun onPause(activity: Activity)

    fun play(songs: List<Song>)
    fun play(index: Int)
    fun resume()
    fun pause()
    fun stop()
    fun changeRepeatMode()
    fun getCurrentPlayingState(): Flow<PlayingState>
    fun getPlayerState(): Flow<PlayerState>
    fun changeShuffleMode()
    fun skipToNext()
    fun skipToPrevious()
    fun seekTo(position: Int)
    fun isPlaying(): Boolean
    fun getPlaylist(): List<Song>
}

internal class MusicPlayerImpl private constructor() : MusicPlayer {
    companion object {
        val instance by lazy { MusicPlayerImpl() }
    }

    private var musicPlayer: MusicService? = null
    private val serviceConnection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            musicPlayer = (service as MusicService.MusicServiceBinder).getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicPlayer = null
        }
    }

    override fun onResume(activity: Activity) {
        if (musicPlayer == null) {
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
        val musicPlayer = musicPlayer ?: return

        if (!musicPlayer.isPlaying()) {
            musicPlayer.stopSelf()
        }

        activity.unbindService(serviceConnection)
        this.musicPlayer = null
    }

    override fun play(songs: List<Song>) {
        musicPlayer?.play(songs)
    }

    override fun play(index: Int) {
        musicPlayer?.play(index)
    }

    override fun resume() {
        musicPlayer?.resume()
    }

    override fun pause() {
        musicPlayer?.pause()
    }

    override fun stop() {
        musicPlayer?.stop()
    }

    override fun changeRepeatMode() {
        musicPlayer?.changeRepeatMode()
    }

    override fun changeShuffleMode() {
        musicPlayer?.changeShuffleMode()
    }

    override fun getCurrentPlayingState(): Flow<PlayingState> {
        val musicPlayer = musicPlayer ?: return flowOf(PlayingState())
        return musicPlayer.getPlayingState()
    }

    override fun getPlayerState():Flow<PlayerState> {
        val musicPlayer = musicPlayer ?: return flowOf(PlayerState())
        return musicPlayer.getPlayerState()
    }

    override fun skipToNext() {
        musicPlayer?.playNextSong()
    }

    override fun skipToPrevious() {
        musicPlayer?.playPreviousSong()
    }

    override fun seekTo(position: Int) {
        musicPlayer?.seekTo(position)
    }

    override fun isPlaying(): Boolean {
        return musicPlayer?.isPlaying() ?: false
    }

    override fun getPlaylist(): List<Song> {
        return musicPlayer?.getPlayList() ?: emptyList()
    }
}
