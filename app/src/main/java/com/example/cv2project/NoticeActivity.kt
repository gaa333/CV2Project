package com.example.cv2project

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cv2project.preferences.Notice
import com.example.cv2project.preferences.NoticePreferences
import com.example.cv2project.ui.theme.CV2ProjectTheme

class NoticeActivity: ComponentActivity() {
    private lateinit var noticePrefs: NoticePreferences
    private lateinit var notices: MutableState<List<Notice>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        noticePrefs = NoticePreferences(this)
        notices = mutableStateOf(noticePrefs.loadNotices()) // 초기 데이터 로드
        setContent {
            CV2ProjectTheme {
                NoticeScreen(notices)
            }
        }
    }
    override fun onResume() {
        super.onResume()
        notices.value = noticePrefs.loadNotices()
    }
}

// 알림장
@Composable
fun NoticeScreen(notices: MutableState<List<Notice>>) {
    val context = LocalContext.current as? Activity
//    var notices by remember { mutableStateOf(noticePrefs.loadNotices()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.LightGray),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.1f))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(color = Color.White),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "알림장",
                color = Color.Black,
                fontSize = 30.sp,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp)
            )
            Image(
                painter = painterResource(R.drawable.pen),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 20.dp)
                    .size(30.dp)
                    .clickable {
                        val intent = Intent(context, AddNoticeActivity::class.java)
                        context?.startActivity(intent)
                    }
            )
            Image(
                painter = painterResource(R.drawable.x),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 15.dp)
                    .size(20.dp)
                    .clickable { context?.finish() }
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 20.dp)
        ) {
            if (notices.value.isEmpty()) {
                Text("등록된 알림이 없습니다.", modifier = Modifier.padding(16.dp))
            } else {
                notices.value.forEach { notice ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
//                        elevation = 4.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("📅 ${notice.date}") // 날짜 표시
                            Text("🎓 학생: ${notice.studentName}")
                            Text("📝 제목: ${notice.title}")
                            Text("📄 내용: ${notice.content}")
                        }
                    }
                }
            }
        }
    }
}