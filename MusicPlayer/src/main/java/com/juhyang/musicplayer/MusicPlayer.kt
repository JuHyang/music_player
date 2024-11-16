package com.juhyang.musicplayer

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.juhyang.musicplayer.internal.MusicService


public class MusicPlayer {
    private var playList: MutableList<Song> = mutableListOf()
    private var currentPlayingIndex: Int? = null
    private var currentPlaying: Song? = null

    private var musicPlayer: MusicService? = null
    private val serviceConnection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d("##Arthur", "MusicPlayer onServiceConnected: bind :")
            musicPlayer = (service as MusicService.MusicServiceBinder).getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicPlayer = null
        }
    }

    fun onResume(activity: Activity) {
        Log.d("##Arthur", "MusicPlayer onResume: bind :")
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

    fun onPause(activity: Activity) {
        val musicPlayer = musicPlayer ?: return

        if (!musicPlayer.isPlaying()) {
            musicPlayer.stopSelf()
        }

        activity.unbindService(serviceConnection)
        this.musicPlayer = null
    }

    fun play(index: Int) {
        if (currentPlayingIndex == null) {
            playSong(playList[index])
            currentPlayingIndex = index
        }
    }

    fun play(songs: List<Song>) {
        playList.clear()
        playList.addAll(songs)
        currentPlayingIndex = null
        play(0)
    }

    private fun playSong(song: Song) {
        musicPlayer?.start(song.filePath)
    }

    fun resume() {
        musicPlayer?.resume()
    }

    fun pause() {
        musicPlayer?.pause()
    }

    fun stop() {
        musicPlayer?.stop()
    }
}
