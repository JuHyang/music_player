package com.juhyang.musicplayer.data.datasource

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.juhyang.musicplayer.data.model.Mp3Data
import com.juhyang.musicplayer.data.model.SongMapper
import com.juhyang.musicplayer.domain.model.Album
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf


class AlbumLocalDataSource(
    private val context: Context
): AlbumDataSource {
    override suspend fun getAlbumList(): Flow<List<Album>> {
        val mp3DataList = getLocalMp3DataList()

        return flowOf(mapToAlbumFromMp3Data(mp3DataList))
    }

    private fun mapToAlbumFromMp3Data(mp3DataList: List<Mp3Data>): List<Album> {
        return mp3DataList.groupBy { Triple(it.albumName, it.artistName, it.albumCoverUri) }
            .map { (key, mp3Files) ->
                val songMapper = SongMapper()
                val songs = mp3Files.map { songMapper.map(it) }

                Album(
                    key.first,
                    key.second,
                    key.third,
                    songs
                )
            }
    }

    private fun getLocalMp3DataList(): List<Mp3Data> {
        val mp3DataList: MutableList<Mp3Data> = mutableListOf()
        val projection = arrayOf(
            MediaStore.Audio.Media.DATA,         // 파일 경로
            MediaStore.Audio.Media.DISPLAY_NAME, // 파일 이름
            MediaStore.Audio.Media.TITLE,        // 제목
            MediaStore.Audio.Media.ALBUM,        // 앨범 이름
            MediaStore.Audio.Media.ARTIST,        // 아티스트 이름
            MediaStore.Audio.Media.ALBUM_ID,      // 앨범 ID (커버 이미지용)
            MediaStore.Audio.Media.DURATION      // 재생 시간
        )

        val selection = "${MediaStore.Audio.Media.MIME_TYPE} = ?"
        val selectionArgs = arrayOf("audio/mpeg") // MP3 MIME 타입

        val cursor: Cursor? = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )

        cursor?.use {
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val nameColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val titleColumn = getSafeColumnIndex(it, MediaStore.Audio.Media.TITLE)
            val albumColumn = getSafeColumnIndex(it, MediaStore.Audio.Media.ALBUM)
            val artistColumn = getSafeColumnIndex(it, MediaStore.Audio.Media.ARTIST)
            val albumIdColumn = getSafeColumnIndex(it, MediaStore.Audio.Media.ALBUM_ID)
            val durationColumn = getSafeColumnIndex(it, MediaStore.Audio.Media.DURATION)

            while (it.moveToNext()) {
                val filePath = it.getString(dataColumn)
                val fileName = it.getString(nameColumn)
                val title = titleColumn?.let { col -> it.getString(col) }
                val albumName = albumColumn?.let { col -> it.getString(col) }
                val artistName = artistColumn?.let { col -> it.getString(col) }
                val albumId = albumIdColumn?.let { col -> it.getLong(col) }
                val duration = durationColumn?.let { col -> it.getLong(col) }

                // 앨범 커버 이미지 URI 생성
                val albumArtUri = albumId?.let { id ->
                    Uri.parse("content://media/external/audio/albumart/$id")
                }

                mp3DataList.add(
                    Mp3Data(
                        title ?: fileName,
                        filePath,
                        albumName ?: "Unknown",
                        artistName ?: "Unknown",
                        albumArtUri,
                        duration
                    )
                )
            }
        }
        return mp3DataList
    }

    private fun getSafeColumnIndex(cursor: Cursor, columnName: String): Int? {
        return try {
            cursor.getColumnIndex(columnName).takeIf { it != -1 }
        } catch (e: Exception) {
            null
        }
    }
}
