# music_player

Compose 를 활용한 간단한 음악 플레이어

<p align="center">  
  <img src="https://github.com/user-attachments/assets/f0c1bb0c-a4a5-4626-9785-2f011db50b51" align="center" width="15%">
  <img src="https://github.com/user-attachments/assets/92337b35-e8c1-4e49-a622-36122f8df2c8" align="center" width="15%">
  <img src="https://github.com/user-attachments/assets/a4895f4f-295b-4ca7-9cfb-d82a00fa150c" align="center" width="15%">
  <img src="https://github.com/user-attachments/assets/443586aa-1850-4816-a63b-ee5a066e2523" align="center" width="15%">
  <img src="https://github.com/user-attachments/assets/bc96aa98-3a6c-4941-8ee1-35c1451b5f1a" align="center" width="15%">
</p>


## 사용 기술
- Compose
- MediaPlayer
- MediaSession
- Coil
  - Picasso / Glide 와 같은 이미지 로딩 라이브러리
  - 위의 두 라이브러리와 달리 Compose 에 최적화되어 있어서 선택.
- Media3 안쓴 이유
  - 뭔가 치트키 같은 기분이라 사용하지 않음.
  - ExoPlayer + Media3 를 사용하면 더 편하게 만들 수 있었을듯 ?

## 구성
- app
  - 실제 구동을 위한 앱
    - 로컬에 있는 음악을 불러와서 목록을 보여줍니다.
- feature
  - MusicPlayer
    - Android Service 를 활용한 음악 플레이어
    - 음악을 재생, 일시정지, 다음곡, 이전곡, 재생목록 등을 제공합니다.
    - Notification 을 통해 음악 재생을 제어할 수 있습니다.
    - MusicPlayerView 를 통해 음악 재생 화면을 제공합니다.
    - 음악 재생 시간을 표시하고, SeekBar 를 통해 음악을 제어할 수 있습니다.
    - 음악 재생 시간이 변경되면, MediaSession 을 통해 Notification 에서도 변경된 시간을 표시합니다.
    - MusicPlayer 를 통해 음악 재생 상태를 제어할 수 있습니다.
      - 이는 싱글톤으로 구현되어 있으며, MusicPlayer.instance 를 통해 접근할 수 있습니다.
    - Media 는 블루투스의 MediaButton 도 지원합니다.
     
```kotlin
// Views
@Composable
fun AlbumThumbnail(albumThumbnail: Uri?, modifier: Modifier) 

@Composable
fun MusicPlayerView(bottomSheetState: BottomSheetState, onExpand: () -> Unit, onCollapse: () -> Unit) 

// Feature
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

// Model
data class Song(
  val title: String,
  val filePath: String,
  val albumCoverUri: Uri?,
  val artistName: String,
  val duration: Long?
)
```
- lib
  - permission
    - 권한 요청을 위한 라이브러리
    - context를 가지고 원하는 권한을 체크하고 요청할 수 있습니다.
    - 플로우에 따라 설정 화면으로 보낼 수 있는 기능을 제공합니다.
    - 현재 미리 선언되어있는 권한은 Permission / ReadAudio 권한입니다. 필요한 경우 Permission 을 상속받아 필요한 권한을 추가할 수 있습니다.
    - 필요한 함수를 호출하고, 권한에 대한 결과를 Flow 로 받을 수 있습니다.

```kotlin
// interface
public interface PermissionChecker {
  companion object {
    val instance: PermissionChecker by lazy { PermissionCheckerImpl.instance }
  }

  fun requestPermissionIfNeeded(context: Context, permission: Permission, isRequired: Boolean): Flow<PermissionResult>
  fun isGrantedPermission(context: Context, permission: Permission): Boolean
}

// Model
enum class GrantStatus {
    GRANTED,
    REVOKED;
}
data class PermissionResult(val permission: String, val grantStatus: GrantStatus)

abstract class Permission {
  abstract val manifestPermission: String

  open fun isNeedToRequestPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(context, manifestPermission) != PackageManager.PERMISSION_GRANTED
  }
}
class NotificationPermission: Permission()
class ReadAudioPermission: Permission() 

```
