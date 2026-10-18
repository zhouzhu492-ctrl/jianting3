package com.jianting.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

// 歌曲数据（用的是公开测试音频，保证能响）
data class Song(val title: String, val artist: String, val url: String, val color: Color)
val mockSongs = listOf(
    Song("晴天", "测试音频 1", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3", Color(0xFFE3F2FD)),
    Song("夜曲", "测试音频 2", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3", Color(0xFFFFF3E0)),
    Song("稻香", "测试音频 3", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3", Color(0xFFE8F5E9))
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    var currentTab by remember { mutableIntStateOf(0) }
    
    // 初始化播放器
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build()
    }
    
    // 播放状态
    var isPlaying by remember { mutableStateOf(false) }
    var currentSongTitle by remember { mutableStateOf("未播放") }
    
    // 监听播放器状态
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlayingNow: Boolean) {
                isPlaying = isPlayingNow
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("简听", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            Column {
                // 迷你播放条（播放时显示）
                if (currentSongTitle != "未播放") {
                    MiniPlayerBar(
                        title = currentSongTitle,
                        isPlaying = isPlaying,
                        onPlayPauseClick = {
                            if (isPlaying) exoPlayer.pause() else exoPlayer.play()
                        }
                    )
                }
                // 底部导航
                NavigationBar {
                    NavigationBarItem(
                        selected = currentTab == 0,
                        onClick = { currentTab = 0 },
                        icon = { Text("🏠", fontSize = 20.sp) },
                        label = { Text("首页") }
                    )
                    NavigationBarItem(
                        selected = currentTab == 1,
                        onClick = { currentTab = 1 },
                        icon = { Text("🔍", fontSize = 20.sp) },
                        label = { Text("搜索") }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (currentTab == 0) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text("推荐歌单", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                    }
                    items(mockSongs) { song ->
                        SongCard(song) {
                            // 点击歌曲开始播放
                            currentSongTitle = song.title
                            exoPlayer.setMediaItem(MediaItem.fromUri(song.url))
                            exoPlayer.prepare()
                            exoPlayer.play()
                        }
                    }
                }
            } else {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("搜索功能开发中...", color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun MiniPlayerBar(title: String, isPlaying: Boolean, onPlayPauseClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(text = "正在播放", fontSize = 12.sp, color = Color.Gray)
            }
            // 播放/暂停按钮
            Text(
                text = if (isPlaying) "⏸️" else "▶️",
                fontSize = 28.sp,
                modifier = Modifier.clickable { onPlayPauseClick() }
            )
        }
    }
}

@Composable
fun SongCard(song: Song, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(song.color, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("🎵", fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = song.title, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = song.artist, fontSize = 14.sp, color = Color.Gray)
            }
        }
    }
}
