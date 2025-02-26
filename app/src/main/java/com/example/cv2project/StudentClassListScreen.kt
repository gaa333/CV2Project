package com.example.cv2project

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.example.cv2project.firebase.StudentDatabase
import com.example.cv2project.models.Student

/**
 * 반 목록 화면 Composable
 * Navigation 그래프에서 route를 "studentClassList"로 등록하여 사용
 */
@Composable
fun StudentClassListScreen(navController: NavController) {
    var classList by remember { mutableStateOf(listOf("6세반", "7세반")) }
    var isAddingClass by remember { mutableStateOf(false) }
    var newClassName by remember { mutableStateOf("") }

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
            // 뒤로가기 버튼
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "뒤로가기",
                modifier = Modifier
                    .padding(start = 15.dp)
                    .size(25.dp)
                    .clickable {
                        // 네비게이션으로 뒤로가기
                        navController.popBackStack()
                    },
                tint = Color.White
            )

            Text(text = "반 목록", fontSize = 25.sp, color = Color.White)

            // 새 반 추가 버튼
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "반 추가",
                modifier = Modifier
                    .padding(end = 15.dp)
                    .size(30.dp)
                    .clickable { isAddingClass = true },
                tint = Color.White
            )
        }

        // 이미지 목록 (예: 1학년반 ~ 6학년반)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // 예: 클릭 시 StudentManagementActivity -> Compose라면, navController.navigate("studentManagement")
            Image(
                painter = painterResource(id = R.drawable.class3),
                contentDescription = "1학년반",
                modifier = Modifier
                    .clickable {
                        navController.navigate("studentManagement?className=1학년반") // 만약 StudentManagementScreen이 있다면
                    }
            )
            Image(
                painter = painterResource(id = R.drawable.class4),
                contentDescription = "2학년반"
            )
            Image(
                painter = painterResource(id = R.drawable.class5),
                contentDescription = "3학년반"
            )
            Image(
                painter = painterResource(id = R.drawable.class6),
                contentDescription = "4학년반"
            )
            Image(
                painter = painterResource(id = R.drawable.class7),
                contentDescription = "5학년반"
            )
            Image(
                painter = painterResource(id = R.drawable.class8),
                contentDescription = "6학년반"
            )
        }

        // 새 반 추가 팝업
        if (isAddingClass) {
            AlertDialog(
                onDismissRequest = { isAddingClass = false },
                title = { Text("새로운 반 추가") },
                text = {
                    OutlinedTextField(
                        value = newClassName,
                        onValueChange = {
                            // 숫자만 입력받고 자동으로 "세반" 붙이기
                            newClassName = it.filter { char -> char.isDigit() }
                            if (newClassName.isNotEmpty()) {
                                newClassName = "${newClassName}세반"
                            }
                        },
                        label = { Text("숫자 입력") }
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newClassName.isNotBlank()) {
                                classList = classList + newClassName
                                newClassName = ""
                                isAddingClass = false
                            }
                        }
                    ) {
                        Text("추가")
                    }
                },
                dismissButton = {
                    Button(onClick = { isAddingClass = false }) {
                        Text("취소")
                    }
                }
            )
        }
    }
}

@Composable
fun StudentManagementScreen(
    navController: NavController,
    studentDb: StudentDatabase,
    selectedClassName: String
) {
    var students by remember { mutableStateOf<List<Student>>(emptyList()) }
    var isAddingStudent by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }

    // Firebase에서 학생 목록 불러오기
    LaunchedEffect(Unit) {
        studentDb.loadStudents(selectedClassName) { loadedStudents ->
            students = loadedStudents
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
                    .clickable { navController.popBackStack() }, // 뒤로가기 처리
                tint = Color.White
            )
            Text(
                text = "학생 프로필",
                color = Color.White,
                fontSize = 25.sp,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "학생 추가",
                modifier = Modifier
                    .padding(end = 15.dp)
                    .size(25.dp)
                    .clickable { isAddingStudent = true }, // 학생 추가 다이얼로그 활성화
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.height(15.dp))

        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            // 학생 리스트 출력
//            students.forEach { student ->
//                StudentCard(student = student) { selectedStudent ->
//                    navController.navigate("studentDetail?studentName=음바페")
//                }
//                Spacer(modifier = Modifier.height(10.dp))
//            }
        }

//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .verticalScroll(rememberScrollState()),
//            verticalArrangement = Arrangement.Top,
//            horizontalAlignment = Alignment.CenterHorizontally,
//        ) {
//            Image(
//                painter = painterResource(id = R.drawable.profile2),
//                contentDescription = "검색",
//                modifier = Modifier.padding(start = 5.dp, end = 5.dp)
//            )
//            Spacer(modifier = Modifier.height(15.dp))
//
//            // ✅ 네비게이션을 사용하여 학생 상세 정보 화면으로 이동
//            Image(
//                painter = painterResource(id = R.drawable.profile3),
//                contentDescription = "음바페",
//                modifier = Modifier
//                    .clickable {
//                        navController.navigate("studentDetail?studentName=음바페")
//                    }
//            )
//            Image(
//                painter = painterResource(id = R.drawable.profile4),
//                contentDescription = "손흥민"
//            )
//            Image(
//                painter = painterResource(id = R.drawable.profile5),
//                contentDescription = "호날두"
//            )
//            Image(
//                painter = painterResource(id = R.drawable.profile6),
//                contentDescription = "메시"
//            )
//            Image(
//                painter = painterResource(id = R.drawable.profile7),
//                contentDescription = "네이마르"
//            )
//        }
    }

    // ✅ 학생 추가 다이얼로그
    if (isAddingStudent) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("이름") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("나이") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            name = ""
                            age = ""
                            isAddingStudent = false
                        }
                    ) {
                        Text("취소")
                    }

                    Button(
                        onClick = {
                            if (name.isNotEmpty() && age.isNotEmpty()) {
                                val newStudent = Student(
                                    id = studentDb.generateStudentId(),
                                    name = name,
                                    age = age.toInt()
                                )

                                // ✅ 기존 학생 목록 가져오기 & 새 학생 추가
                                studentDb.loadStudents(selectedClassName) { existingStudents ->
                                    val updatedStudents = existingStudents + newStudent

                                    // ✅ Firebase에 저장
                                    studentDb.saveStudents(
                                        selectedClassName,
                                        updatedStudents
                                    ) { success ->
                                        if (success) {
                                            students = updatedStudents // ✅ UI 업데이트
                                            name = ""
                                            age = ""
                                            isAddingStudent = false
                                        }
                                    }
                                }
                            }
                        }
                    ) {
                        Text("저장")
                    }
                }
            }
        }
    }
}

@Composable
fun StudentDetailScreen(navController: NavController, studentName: String, studentAge: Int) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.detail1),
            contentDescription = "$studentName 프로필",
            Modifier.fillMaxWidth()
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 5.dp, end = 5.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Image(
                painter = painterResource(id = R.drawable.detail2),
                contentDescription = "출석"
            )
            Image(
                painter = painterResource(id = R.drawable.detail3),
                contentDescription = "특이사항"
            )
            Image(
                painter = painterResource(id = R.drawable.detail4),
                contentDescription = "원비 결제"
            )
            Image(
                painter = painterResource(id = R.drawable.detail5),
                contentDescription = "연락처"
            )
            Image(
                painter = painterResource(id = R.drawable.detail6),
                contentDescription = "주소"
            )
            Image(
                painter = painterResource(id = R.drawable.detail7),
                contentDescription = "성과리포트"
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
