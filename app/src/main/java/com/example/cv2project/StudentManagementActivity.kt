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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
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
                .height(60.dp)
                .background(color = Color.White),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "학생 관리",
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
                        isAddingStudent = true
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
            // 학생 리스트
            students.forEach { student ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
//                    elevation = 4.dp
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
                            painter = painterResource(id = R.drawable.delete), // 휴지통 이미지
                            contentDescription = "삭제",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    selectedStudent = student
                                    showDialog = true
                                }
                        )

                        if (showDialog) {
                            AlertDialog(
                                onDismissRequest = { showDialog = false },
                                title = { Text("학생 정보 삭제") },
                                text = { Text("학생 정보가 영구적으로 삭제됩니다. \n삭제하시겠습니까?") },
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            selectedStudent?.let { studentToDelete ->
                                                val updatedStudents = students.toMutableList()
                                                updatedStudents.remove(studentToDelete) // 학생 객체를 직접 삭제
                                                studentPrefs.saveStudents(updatedStudents)
                                                students = updatedStudents
                                            }
                                            showDialog = false // 다이얼로그 닫기
                                            selectedStudent = null
                                        }                                    ) {
                                        Text("삭제")
                                    }
                                },
                                dismissButton = {
                                    Button(
                                        onClick = {
                                            showDialog = false
                                            selectedStudent = null
                                        }
                                    ) {
                                        Text("취소")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
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