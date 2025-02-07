package com.example.cv2project

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.cv2project.ui.theme.CV2ProjectTheme

class StudentDetailActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Intent에서 데이터 가져오기
        val studentName = intent.getStringExtra("student_name") ?: "이름 없음"
        val studentAge = intent.getIntExtra("student_age", 0)
        setContent {
            CV2ProjectTheme {
                StudentDetailScreen()
            }
        }
    }
}

@Composable
fun StudentDetailScreen() {
    val context = LocalContext.current as? Activity
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ✅ 학생 이미지들 표시
        Image(
            painter = painterResource(id = R.drawable.student1),
            contentDescription = "student1",
            Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))

        Image(
            painter = painterResource(id = R.drawable.student2),
            contentDescription = "student2",
            Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.student3),
            contentDescription = "student3",
            Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.student4),
            contentDescription = "student4",
            Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.student5),
            contentDescription = "student5",
            Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))

        Image(
            painter = painterResource(id = R.drawable.student6),
            contentDescription = "student6",
            Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
    }
}
