package com.example.cv2project

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cv2project.preferences.Notice
import com.example.cv2project.preferences.NoticePreferences
import com.example.cv2project.preferences.Student
import com.example.cv2project.preferences.StudentPreferences
import com.example.cv2project.ui.theme.CV2ProjectTheme

class AddNoticeActivity : ComponentActivity() {
    private lateinit var studentPrefs: StudentPreferences
    private lateinit var noticePrefs: NoticePreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        studentPrefs = StudentPreferences(this)
        noticePrefs = NoticePreferences(this)
        setContent {
            CV2ProjectTheme {
                AddNoticeScreen(studentPrefs, noticePrefs, onNoticeAdded = { finish() })
            }
        }
    }
}

@Composable
fun AddNoticeScreen(
    studentPrefs: StudentPreferences,
    noticePrefs: NoticePreferences,
    onNoticeAdded: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var students by remember { mutableStateOf(studentPrefs.loadAllStudents()) }
    var selectedStudent by remember { mutableStateOf<Student?>(null) }
    val todayDate = getTodayDate() // 오늘 날짜

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center
    ) {
        Text("알림장 작성", fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))

        Text("학생 선택")
        students.forEach { student ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedStudent = student }
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = student.name)
                if (selectedStudent == student) {
                    Text("✔️")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("알림 제목") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("알림 내용") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (title.isNotEmpty() && content.isNotEmpty() && selectedStudent != null) {
                    val newNotice = Notice(title, content, selectedStudent!!.name, todayDate)
                    val updatedNotices = noticePrefs.loadNotices().toMutableList()
                    updatedNotices.add(newNotice)
                    noticePrefs.saveNotices(updatedNotices)
                    onNoticeAdded()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("저장")
        }
    }
}