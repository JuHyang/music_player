package com.juhyang.musicplayer

import android.app.Activity
import com.juhyang.musicplayer.internal.model.PlayerState
import com.juhyang.musicplayer.internal.model.PlayingState
import com.juhyang.musicplayer.internal.presentation.MusicPlayerImpl
import kotlinx.coroutines.flow.StateFlow


interface MusicPlayer {
    companion object {
        val instance: MusicPlayer by lazy { MusicPlayerImpl.instance }
    }

    fun onStart(activity: Activity)
    fun onResume(activity: Activity)
    fun onPause(activity: Activity)

    fun play(songs: List<Song>)
    fun play(index: Int)
    fun resume()
    fun pause()
    fun stop()
    fun changeRepeatMode()
    fun getPlayingState(): StateFlow<PlayingState>
    fun getPlayerState(): StateFlow<PlayerState>
    fun changeShuffleMode()
    fun skipToNext()
    fun skipToPrevious()
    fun seekTo(position: Int)
    fun isPlaying(): Boolean
    fun getPlaylist(): List<Song>
    fun addPlayList(song: Song)
}

