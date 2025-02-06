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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import com.example.cv2project.ui.theme.CV2ProjectTheme

class DetailNoticeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 전달된 데이터 받기
        val title = intent.getStringExtra("title") ?: "제목 없음"
        val content = intent.getStringExtra("content") ?: "내용 없음"
        val studentName = intent.getStringExtra("studentName") ?: "이름 없음"
        val date = intent.getStringExtra("date") ?: "날짜 없음"
        setContent {
            CV2ProjectTheme {
                DetailNoticeScreen(
                    title,
                    content,
                    studentName,
                    date
                )
            }
        }
    }
}

@Composable
fun DetailNoticeScreen(title: String, content: String, studentName: String, date: String) {
    val context = LocalContext.current as? Activity
    var showDialog by remember { mutableStateOf(false) }


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
                "알림장 내용",
                color = Color.Black,
                fontSize = 30.sp,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp)
            )
            Image(
                painter = painterResource(R.drawable.delete),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 20.dp)
                    .size(30.dp)
                    .clickable { showDialog = true }
            )
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("알림장 삭제") },
                    text = { Text("내용이 영구적으로 삭제됩니다. \n삭제하시겠습니까?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                val resultIntent = Intent().apply {
                                    putExtra("delete_title", title)
                                    putExtra("delete_content", content)
                                    putExtra("delete_studentName", studentName)
                                    putExtra("delete_date", date)
                                }
                                // 현재 컨텍스트를 Activity로 캐스팅하여 setResult와 finish 호출
                                context?.apply {
                                    setResult(Activity.RESULT_OK, resultIntent)
                                    finish()
                                }
                            }
                        ) {
                            Text("삭제")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showDialog = false }
                        ) {
                            Text("취소")
                        }
                    }
                )
            }
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
                .padding(20.dp)
        ) {
            // 알림장 세부 내용
            Spacer(modifier = Modifier.size(10.dp))
            // 학생명
            Text(
                text = "$studentName 학생",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 작성 날짜
            Text(
                text = "작성 날짜: $date",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 공지사항 제목
            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 공지사항 내용
            Text(text = content, fontSize = 18.sp)
        }
    }

}
