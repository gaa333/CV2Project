package com.example.cv2project

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cv2project.preferences.Notice
import com.example.cv2project.preferences.NoticePreferences

/**
 * 네비게이션으로 사용할 Composable
 * - route 예: "notice"
 */
@Composable
fun NoticeScreen(navController: NavController) {
    val context = LocalContext.current as Activity
    val noticePrefs = remember { NoticePreferences(context) }
    val notices = remember { mutableStateOf(noticePrefs.loadNotices()) }

    // 다른 화면에서 돌아올 때마다 목록을 새로고침하고 싶다면
    LaunchedEffect(Unit) {
        notices.value = noticePrefs.loadNotices()
    }

    // Composable UI
    NoticeContent(
        navController = navController,
        notices = notices,
        noticePrefs = noticePrefs
    )
}

/**
 * 실제 UI 구성을 담당하는 함수
 */
@Composable
fun NoticeContent(
    navController: NavController,
    notices: MutableState<List<Notice>>,
    noticePrefs: NoticePreferences
) {
    val context = LocalContext.current as? Activity

    // DetailNoticeActivity에서 데이터를 받아오기 위한 launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val title = result.data?.getStringExtra("notice_title") ?: ""
            val content = result.data?.getStringExtra("notice_content") ?: ""
            val studentName = result.data?.getStringExtra("notice_studentName") ?: ""
            val date = result.data?.getStringExtra("notice_date") ?: ""

            // 새 알림 저장
            if (title.isNotEmpty() && content.isNotEmpty() && studentName.isNotEmpty() && date.isNotEmpty()) {
                val newNotice = Notice(title, content, studentName, date)
                notices.value = notices.value + newNotice
                noticePrefs.saveNotices(notices.value.toList())
            }

            // 알림 삭제 요청 처리
            val deleteTitle = result.data?.getStringExtra("delete_title")
            val deleteContent = result.data?.getStringExtra("delete_content")
            val deleteStudent = result.data?.getStringExtra("delete_studentName")
            val deleteDate = result.data?.getStringExtra("delete_date")

            if (deleteTitle != null && deleteContent != null && deleteStudent != null && deleteDate != null) {
                notices.value = notices.value.filterNot {
                    it.title == deleteTitle &&
                            it.content == deleteContent &&
                            it.studentName == deleteStudent &&
                            it.date == deleteDate
                }
                noticePrefs.saveNotices(notices.value.toList())
            }
        }
    }

    // UI
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 상단 Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(color = Color.Black),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 뒤로 가기 버튼: navController.popBackStack() 사용
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "뒤로가기",
                modifier = Modifier
                    .padding(start = 15.dp)
                    .size(25.dp)
                    .clickable {
                        navController.popBackStack() // 뒤로가기
                    },
                tint = Color.White
            )

            Text(
                "알림장",
                fontSize = 25.sp,
                color = Color.White
            )

            // 알림 추가 버튼
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 15.dp)
                    .size(30.dp)
                    .clickable {
                        // AddNoticeActivity 이동
                        val intent = Intent(context, AddNoticeActivity::class.java)
                        context?.startActivity(intent)
                    },
                tint = Color.White
            )
        }

        // 알림 목록
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 20.dp)
        ) {
            if (notices.value.isEmpty()) {
                Text("등록된 알림이 없습니다.", modifier = Modifier.padding(16.dp))
            } else {
                notices.value.forEach { notice ->
                    androidx.compose.material3.Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                val intent = Intent(context, DetailNoticeActivity::class.java).apply {
                                    putExtra("title", notice.title)
                                    putExtra("content", notice.content)
                                    putExtra("studentName", notice.studentName)
                                    putExtra("date", notice.date)
                                }
                                launcher.launch(intent)
                            },
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("📅 ${notice.date}")
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
