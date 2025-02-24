package com.example.cv2project

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.example.cv2project.preferences.Student
import com.example.cv2project.preferences.StudentPreferences

/**
 * 성과 보고서 Composable
 * - Navigation에서 route를 "performanceReport"로 등록해두고 navController.navigate("performanceReport")로 이동
 */
@Composable
fun PerformanceReportScreen(navController: NavController, studentPrefs: StudentPreferences) {
    // 기존 Activity에서 studentPrefs를 받았으므로, 외부에서 DI 해주는 방식으로 변경
    var students by remember { mutableStateOf(studentPrefs.loadAllStudents()) }

    Column(
        modifier = Modifier.fillMaxSize(),
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
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "뒤로가기",
                modifier = Modifier
                    .padding(start = 15.dp)
                    .size(25.dp)
                    .clickable {
                        // Navigation 뒤로가기
                        navController.popBackStack()
                    },
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

        // 본문
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            // 학생 리스트 출력
            students.forEach { student ->
                StudentCard(student = student) { selectedStudent ->
                    // 만약 Compose Navigation만 사용한다면:
                    // navController.navigate("detailPerformance/${selectedStudent.name}/${selectedStudent.age}")
                    // 등으로 전달

                    // 기존 Activity를 호출하는 경우:
//                    val context = LocalContext.current
//                    val intent = Intent(context, DetailPerformanceReportActivity::class.java).apply {
//                        putExtra("name", selectedStudent.name)
//                        putExtra("age", selectedStudent.age)
//                    }
//                    context.startActivity(intent)
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

/**
 * 학생 카드
 */
@Composable
fun StudentCard(student: Student, onClick: (Student) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .clickable { onClick(student) },
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
