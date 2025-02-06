package com.example.cv2project

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.cv2project.preferences.AnnouncementPreferences
import com.example.cv2project.ui.theme.CV2ProjectTheme

class NoticeActivity: ComponentActivity() {
    private lateinit var announcementPrefs: AnnouncementPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        announcementPrefs = AnnouncementPreferences(this)
        setContent {
            CV2ProjectTheme {
                NoticeScreen(announcementPrefs)
            }
        }
    }
}

// 알림장
@Composable
fun NoticeScreen(announcementPrefs: AnnouncementPreferences) {
    val context = LocalContext.current as? Activity

    var announcements by remember { mutableStateOf(announcementPrefs.loadAnnouncements()) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val title = result.data?.getStringExtra("announcement_title") ?: ""
            val content = result.data?.getStringExtra("announcement_content") ?: ""
            val date = result.data?.getStringExtra("announcement_date") ?: ""

            if (title.isNotEmpty() && content.isNotEmpty() && date.isNotEmpty()) {
                announcements = announcements + Triple(title, content, date)

                // 변경된 공지사항을 SharedPreferences에 저장
                announcementPrefs.saveAnnouncements(announcements)
            }
            //  삭제 요청 확인
            val deleteTitle = result.data?.getStringExtra("delete_title")
            val deleteContent = result.data?.getStringExtra("delete_content")
            val deleteDate = result.data?.getStringExtra("delete_date")

            if (deleteTitle != null && deleteContent != null && deleteDate != null) {
                announcements = announcements.filterNot {
                    it.first == deleteTitle && it.second == deleteContent && it.third == deleteDate
                }
                announcementPrefs.saveAnnouncements(announcements) // 저장소 업데이트
            }
        }
    }

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
                        launcher.launch(intent)
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
            announcements.forEach { announcement ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            val intent = Intent(context, DetailAnnouncementActivity::class.java).apply {
                                putExtra("announcement_title", announcement.first)
                                putExtra("announcement_content", announcement.second)
                                putExtra("announcement_date", announcement.third)
                            }
                            launcher.launch(intent) // DetailAnnouncementActivity 실행
                        }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = announcement.first, // 제목
                            fontSize = 18.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = announcement.second, fontSize = 14.sp) // 내용
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = announcement.third, // 날짜
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }

}