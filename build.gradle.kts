package com.jianting.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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

// 模拟的歌曲数据（以后接入网易云就换成真实数据）
data class Song(val title: String, val artist: String, val color: Color)
val mockSongs = listOf(
    Song("晴天", "周杰伦", Color(0xFFE3F2FD)),
    Song("夜曲", "周杰伦", Color(0xFFFFF3E0)),
    Song("稻香", "周杰伦", Color(0xFFE8F5E9)),
    Song("反方向的钟", "周杰伦", Color(0xFFFCE4EC)),
    Song("七里香", "周杰伦", Color(0xFFF3E5F5)),
    Song("告白气球", "周杰伦", Color(0xFFFFEBEE))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var currentTab by remember { mutableIntStateOf(0) }

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
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (currentTab == 0) {
                // 这是首页内容
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "推荐歌单",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    items(mockSongs) { song ->
                        SongCard(song)
                    }
                }
            } else {
                // 这是搜索页内容
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("搜索功能开发中...", color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun SongCard(song: Song) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧的方形封面占位
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(song.color, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("🎵", fontSize = 24.sp)
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // 右侧的文字
            Column {
                Text(text = song.title, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = song.artist, fontSize = 14.sp, color = Color.Gray)
            }
        }
    }
}
