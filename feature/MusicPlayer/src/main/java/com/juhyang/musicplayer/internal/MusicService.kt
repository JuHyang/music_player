package com.juhyang.musicplayer.internal

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat


internal class MusicService: Service() {

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
        Log.d("##Arthur", "MusicService onCreate")
        startForegroundService()
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
        Log.d("##Arthur", "MusicService onStartCommand: onStartCommand")
        startForegroundService()
        return START_STICKY
    }

    override fun onDestroy() {
        mediaPlayer.stop()
        super.onDestroy()
    }

    fun start(filePath: String) {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(filePath)
        mediaPlayer.prepare()
        mediaPlayer.start()
    }

    fun stop() {
        mediaPlayer.stop()
        // service 종료?
    }

    fun resume() {
        mediaPlayer.start()
    }

    fun pause() {
        mediaPlayer.pause()
    }

    fun isPlaying() : Boolean {
        return true
    }
}


