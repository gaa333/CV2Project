package com.example.cv2project

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cv2project.preferences.Student
import com.example.cv2project.preferences.StudentPreferences
import com.example.cv2project.ui.theme.CV2ProjectTheme

class StudentManagementActivity : ComponentActivity() {
    private lateinit var studentPrefs: StudentPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        studentPrefs = StudentPreferences(this)
        setContent {
            CV2ProjectTheme {
                StudentManagementScreen(studentPrefs)
            }
        }
    }
}

@Composable
fun StudentManagementScreen(studentPrefs: StudentPreferences) {
    val context = LocalContext.current as? Activity
    var students by remember { mutableStateOf(studentPrefs.loadStudents()) }
    var isAddingStudent by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var selectedStudent by remember { mutableStateOf<Student?>(null) }

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
                .height(50.dp)
                .background(color = Color.White),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 15.dp)
                    .size(25.dp)
                    .clickable { context?.finish() }
            )
            Text(
                "학생 관리",
                color = Color.Black,
                fontSize = 25.sp,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 15.dp)
                    .size(25.dp)
                    .clickable {
                        isAddingStudent = true
                    }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 20.dp)
        ) {
            val context = LocalContext.current

            students.forEach { student ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            val intent = Intent(context, StudentDetailActivity::class.java).apply {
                                putExtra("student_name", student.name)
                                putExtra("student_age", student.age)
                            }
                            context.startActivity(intent)
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${student.name}, ${student.age}세")

                        Image(
                            painter = painterResource(id = R.drawable.delete),
                            contentDescription = "삭제",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    selectedStudent = student
                                    showDialog = true
                                }
                        )
                    }
                }
            }
        }
    }

    // ✅ 삭제 확인 다이얼로그 추가
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("학생 삭제") },
            text = { Text("정말로 ${selectedStudent?.name} 학생을 삭제하시겠습니까?") },
            confirmButton = {
                Button(
                    onClick = {
                        selectedStudent?.let { student ->
                            val updatedStudents = students.toMutableList()
                            updatedStudents.remove(student) // ✅ 리스트에서 삭제
                            studentPrefs.saveStudents(updatedStudents) // ✅ 저장
                            students = updatedStudents // ✅ 상태 업데이트
                        }
                        showDialog = false
                    }
                ) {
                    Text("삭제")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("취소")
                }
            }
        )
    }

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
                                val updatedStudents = students.toMutableList()
                                updatedStudents.add(Student(name, age.toInt()))
                                studentPrefs.saveStudents(updatedStudents)
                                students = updatedStudents
                                name = ""
                                age = ""
                                isAddingStudent = false
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
