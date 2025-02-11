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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.cv2project.ui.theme.CV2ProjectTheme

class StudentDetailActivity : ComponentActivity() {
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
            .background(Color.LightGray),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.detail1),
            contentDescription = "음바페 프로필",
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
