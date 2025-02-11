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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cv2project.preferences.Student
import com.example.cv2project.preferences.StudentPreferences
import com.example.cv2project.ui.theme.CV2ProjectTheme

class PerformanceReportActivity : ComponentActivity() {
    private lateinit var studentPrefs: StudentPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        studentPrefs = StudentPreferences(this)
        setContent {
            CV2ProjectTheme {
                PerformanceReportScreen(studentPrefs)
            }
        }
    }
}

// 성과 보고서
@Composable
fun PerformanceReportScreen(studentPrefs: StudentPreferences) {
    val context = LocalContext.current as? Activity
    var students by remember { mutableStateOf(studentPrefs.loadAllStudents()) }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(color = Color.Black),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Absolute.SpaceBetween
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "뒤로가기",
                modifier = Modifier
                    .padding(start = 15.dp)
                    .size(25.dp)
                    .clickable { context?.finish() },
                tint = Color.White
            )
            Text("성과보고서", color = Color.White, fontSize = 25.sp)
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "공유",
                modifier = Modifier
                    .padding(end = 15.dp)
                    .size(25.dp),
                tint = Color.White
            )
        }
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            // 학생 리스트..
            students.forEach { student ->
                StudentCard(student = student) { selectedStudent ->
                    val intent =
                        Intent(context, DetailPerformanceReportActivity::class.java).apply {
                            putExtra("name", selectedStudent.name)
                            putExtra("age", selectedStudent.age)
                        }
                    context?.startActivity(intent)
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

// ✅ 학생 카드 Composable
@Composable
fun StudentCard(student: Student, onClick: (Student) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .clickable { onClick(student) }, // ✅ 카드 클릭 이벤트 추가
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(student.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("나이: ${student.age}", fontSize = 14.sp)
        }
    }
}
