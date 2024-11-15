package com.juhyang.musicplayer

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import com.juhyang.musicplayer.internal.MusicService


public class MusicPlayer {
    private var musicPlayer: MusicService? = null
    private val serviceConnection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            musicPlayer = (service as MusicService.MusicServiceBinder).getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicPlayer = null
        }

    }

    fun onResume(activity: Activity) {
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


    fun start(filePath: String) {
        musicPlayer?.start(filePath)
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
