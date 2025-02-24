package com.example.cv2project

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*
import androidx.navigation.NavController

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
                        // navController.navigate("studentManagement") // 만약 StudentManagementScreen이 있다면
                        // 또는 기존 Activity로 이동하는 경우:
                        // val context = LocalContext.current
                        // val intent = Intent(context, StudentManagementActivity::class.java)
                        // context.startActivity(intent)
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
