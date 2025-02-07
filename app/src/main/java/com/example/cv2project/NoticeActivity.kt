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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
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
                NoticeScreen(notices, noticePrefs)
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
fun NoticeScreen(notices: MutableState<List<Notice>>, noticePrefs: NoticePreferences) {
    val context = LocalContext.current as? Activity
//    var notices by remember { mutableStateOf(noticePrefs.loadNotices()) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val title = result.data?.getStringExtra("notice_title") ?: ""
            val content = result.data?.getStringExtra("notice_content") ?: ""
            val studentName = result.data?.getStringExtra("notice_studentName") ?: ""
            val date = result.data?.getStringExtra("notice_date") ?: ""

            if (title.isNotEmpty() && content.isNotEmpty() && studentName.isNotEmpty() && date.isNotEmpty()) {
                val newNotice = Notice(title, content, studentName, date)
                notices.value = notices.value + newNotice // ✅ .value를 사용하여 리스트 업데이트

                // 변경된 알림을 SharedPreferences에 저장
                noticePrefs.saveNotices(notices.value.toList()) // ✅ MutableState 대신 List로 변환하여 저장
            }

            // 삭제 요청 확인
            val deleteTitle = result.data?.getStringExtra("delete_title")
            val deleteContent = result.data?.getStringExtra("delete_content")
            val deleteStudent = result.data?.getStringExtra("delete_studentName")
            val deleteDate = result.data?.getStringExtra("delete_date")

            if (deleteTitle != null && deleteContent != null && deleteStudent != null && deleteDate != null) {
                notices.value = notices.value.filterNot {
                    it.title == deleteTitle && it.content == deleteContent && it.studentName == deleteStudent && it.date == deleteDate
                }
                noticePrefs.saveNotices(notices.value.toList()) // ✅ 저장소 업데이트
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
//            .background(color = Color.LightGray),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(color = Color.LightGray),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "뒤로가기",
                modifier = Modifier
                    .padding(start = 15.dp)
                    .size(25.dp)
                    .clickable { context?.finish() }
            )
            Text(
                "알림장",
                fontSize = 25.sp,
            )
            Image(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 15.dp)
                    .size(30.dp)
                    .clickable {
                        val intent = Intent(context, AddNoticeActivity::class.java)
                        context?.startActivity(intent)
                    }
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