package com.example.cv2project

import android.content.Intent
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.example.cv2project.firebase.StudentDatabase
import com.example.cv2project.models.Student

/**
 * 성과 보고서 Composable
 * - Navigation에서 route를 "performanceReport"로 등록해두고 navController.navigate("performanceReport")로 이동
 */
// 학생 목록
@Composable
fun PerformanceReportScreen(navController: NavController, studentDb: StudentDatabase) {
    // 기존 Activity에서 studentPrefs를 받았으므로, 외부에서 DI 해주는 방식으로 변경
    var students by remember { mutableStateOf<List<Student>>(emptyList()) }

    // Firebase에서 학생 목록 불러오기
    LaunchedEffect(Unit) {
        studentDb.loadAllStudents { fetchedStudents ->
            students = fetchedStudents
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = R.drawable.back),
                contentDescription = "뒤로가기",
                modifier = Modifier
                    .padding(start = 15.dp)
                    .size(25.dp)
                    .clickable {
                        navController.popBackStack()
                    }
            )
            Spacer(modifier = Modifier.weight(0.8f))
            Image(
                painter = painterResource(id = R.drawable.report1),
                contentDescription = "성과보고서",
                modifier = Modifier.size(200.dp)
            )
            Spacer(modifier = Modifier.weight(1.2f))
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
                    navController.navigate("detailPerformanceReport?name=${student.name}&age=${student.age}")

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

// 성과 보고서
@Composable
fun DetailPerformanceReportScreen(
    navController: NavController,
    id: String,  // ✅ 학생 ID 추가
    name: String,
    age: Int
) {
    var student by remember { mutableStateOf<Student?>(null) }

    // ✅ Firebase에서 학생 정보 가져오기
    LaunchedEffect(id) {
        StudentDatabase().getStudentById(id) { fetchedStudent ->
            student = fetchedStudent
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = R.drawable.back),
                contentDescription = "뒤로가기",
                modifier = Modifier
                    .padding(start = 15.dp)
                    .size(25.dp)
                    .clickable {
                        navController.popBackStack()
                    }
            )
            Spacer(modifier = Modifier.weight(0.8f))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
//                    student?.name ?: name,  // ✅ Firebase에서 가져온 데이터가 있으면 사용
                    "고쳐야됨",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .height(2.dp)
                        .width(150.dp)
                        .background(Color(0xFFFF7800))
                )
            }
            Spacer(modifier = Modifier.weight(1.2f))
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            ReportCard("2025년 02월 1일", R.drawable.poseresult0, "163.9 도", "105.9 도", "106.1 도")
            ReportCard("2025년 02월 11일", R.drawable.poseresult, "126.2 도", "88.8 도", "142.6 도")

            Spacer(modifier = Modifier.height(30.dp))
            Text(
                "보고서",
                fontSize = 25.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 5.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text("2월 1일 당시 슈팅 자세에 비해 자세가 많이 개선되었습니다.", modifier = Modifier.padding(start = 10.dp))
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun ReportCard(date: String, imageRes: Int, hip: String, knee: String, ankle: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(450.dp)
    ) {
        Text(
            date,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 50.dp)
        )
        Image(
            painter = painterResource(imageRes),
            contentDescription = null,
            Modifier.size(350.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.28f)
            ) {
                Spacer(modifier = Modifier.size(10.dp))
                Text("Hip Angle")
                Spacer(modifier = Modifier.size(5.dp))
                Text("Knee Angle")
                Spacer(modifier = Modifier.size(5.dp))
                Text("Ankle Angle")
            }

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.3f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("적정 각도", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.size(10.dp))
                Text("150 도")
                Spacer(modifier = Modifier.size(5.dp))
                Text("105 도")
                Spacer(modifier = Modifier.size(5.dp))
                Text("180 도")
            }

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.45f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("측정 각도", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.size(10.dp))
                Text(hip)
                Spacer(modifier = Modifier.size(5.dp))
                Text(knee)
                Spacer(modifier = Modifier.size(5.dp))
                Text(ankle)
            }
        }
    }
}