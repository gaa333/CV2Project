package com.example.cv2project

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cv2project.preferences.AnnouncementPreferences

/**
 * 공지사항 Composable
 * 네비게이션에서 route를 "announcement"로 등록해서 사용
 */
@Composable
fun AnnouncementScreen(navController: NavController) {
    // Activity Context
    val context = LocalContext.current as Activity

    // SharedPreferences를 이용한 공지사항 로드
    val announcementPrefs = remember { AnnouncementPreferences(context) }
    var announcements by remember { mutableStateOf(announcementPrefs.loadAnnouncements()) }

    // 다른 화면에서 돌아올 때마다 목록을 새로 고침하고 싶다면
    // LaunchedEffect(Unit) { announcements = announcementPrefs.loadAnnouncements() }

    // Composable에서 ActivityResult를 사용하는 launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val title = result.data?.getStringExtra("announcement_title") ?: ""
            val content = result.data?.getStringExtra("announcement_content") ?: ""
            val date = result.data?.getStringExtra("announcement_date") ?: ""

            // 새 공지사항 추가
            if (title.isNotEmpty() && content.isNotEmpty() && date.isNotEmpty()) {
                announcements = announcements + Triple(title, content, date)
                announcementPrefs.saveAnnouncements(announcements)
            }

            // 삭제 요청 확인
            val deleteTitle = result.data?.getStringExtra("delete_title")
            val deleteContent = result.data?.getStringExtra("delete_content")
            val deleteDate = result.data?.getStringExtra("delete_date")

            if (deleteTitle != null && deleteContent != null && deleteDate != null) {
                announcements = announcements.filterNot {
                    it.first == deleteTitle &&
                            it.second == deleteContent &&
                            it.third == deleteDate
                }
                announcementPrefs.saveAnnouncements(announcements)
            }
        }
    }

    // UI 구성
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 상단 바
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(color = Color.Black),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 뒤로가기 버튼
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "뒤로가기",
                modifier = Modifier
                    .padding(start = 15.dp)
                    .size(25.dp)
                    .clickable {
                        navController.popBackStack() // 뒤로 가기
                    },
                tint = Color.White
            )

            Text(
                "공지사항",
                color = Color.White,
                fontSize = 25.sp,
            )

            // 공지 추가 버튼
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 15.dp)
                    .size(30.dp)
                    .clickable {
                        val intent = Intent(context, AddAnnouncementActivity::class.java)
                        launcher.launch(intent) // AddAnnouncementActivity 실행
                    },
                tint = Color.White
            )
        }

        // 공지사항 목록
        Column(
            modifier = Modifier
                .padding(10.dp)
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
                            fontWeight = FontWeight.Bold
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
