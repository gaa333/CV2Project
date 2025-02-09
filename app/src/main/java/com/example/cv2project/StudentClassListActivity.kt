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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
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
import com.example.cv2project.ui.theme.CV2ProjectTheme

class StudentClassListActivity:ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CV2ProjectTheme {
                StudentClassListScreen()
            }
        }
    }
}

@Composable
fun StudentClassListScreen() {
    val context = LocalContext.current as? Activity
//    var classList by remember { mutableStateOf(listOf("6세반", "7세반")) }
    var isAddingClass by remember { mutableStateOf(false) }
//    var newClassName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
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
                text = "반 목록",
                fontSize = 25.sp
            )
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "반 추가",
                modifier = Modifier
                    .padding(end = 15.dp)
                    .size(30.dp)
                    .clickable { isAddingClass = true }
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(id = R.drawable.class2),
                contentDescription = "7세반",
                modifier = Modifier
                    .clickable {
                        val intent = Intent(context, StudentManagementActivity::class.java)
                        val className = "7세반"
                        intent.putExtra("class_name", className)
                        context?.startActivity(intent)
                    }
            )
            Image(
                painter = painterResource(id = R.drawable.class3),
                contentDescription = "1학년반",
            )
            Image(
                painter = painterResource(id = R.drawable.class4),
                contentDescription = "2학년반",
            )
            Image(
                painter = painterResource(id = R.drawable.class5),
                contentDescription = "3학년반",
            )
            Image(
                painter = painterResource(id = R.drawable.class6),
                contentDescription = "4학년반",
            )
            Image(
                painter = painterResource(id = R.drawable.class7),
                contentDescription = "5학년반",
            )
            Image(
                painter = painterResource(id = R.drawable.class8),
                contentDescription = "6학년반",
            )
        }

//        classList.forEach { className ->
//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(8.dp)
//                    .clickable {
//                        val intent = Intent(context, StudentManagementActivity::class.java)
//                        intent.putExtra("class_name", className) // 반 이름 전달
//                        context?.startActivity(intent)
//                    }
//            ) {
//                Text(
//                    text = className,
//                    fontSize = 20.sp,
//                    modifier = Modifier.padding(16.dp)
//                )
//            }
//        }

//        if (isAddingClass) {
//            AlertDialog(
//                onDismissRequest = { isAddingClass = false },
//                title = { Text("새로운 반 추가") },
//                text = {
//                    OutlinedTextField(
//                        value = newClassName,
//                        onValueChange = {
//                            newClassName = it.filter { char -> char.isDigit() }
//                            if (newClassName.isNotEmpty()) {
//                                newClassName = "${newClassName}세반"
//                            }
//                        },
//                        label = { Text("숫자 입력") }
//                    )
//                },
//                confirmButton = {
//                    Button(
//                        onClick = {
//                            if (newClassName.isNotBlank()) {
//                                classList = classList + newClassName
//                                newClassName = ""
//                                isAddingClass = false
//                            }
//                        }
//                    ) {
//                        Text("추가")
//                    }
//                },
//                dismissButton = {
//                    Button(onClick = { isAddingClass = false }) {
//                        Text("취소")
//                    }
//                }
//            )
//        }
    }
}
