package com.example.cv2project

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.cv2project.ui.theme.CV2ProjectTheme

class StudentDetailActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CV2ProjectTheme {
                StudentDetailScreen()
            }
        }
    }
}

// 학생관리 - 프로필
@Composable
fun StudentDetailScreen() {
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
        Spacer(modifier = Modifier.height(32.dp))
        Image(
            painter = painterResource(id = R.drawable.detail2),
            contentDescription = "출석")
        Image(
            painter = painterResource(id = R.drawable.detail3),
            contentDescription = "특이사항")
        Image(
            painter = painterResource(id = R.drawable.detail4),
            contentDescription = "원비 결제")
        Image(
            painter = painterResource(id = R.drawable.detail5),
            contentDescription = "연락처")
        Image(
            painter = painterResource(id = R.drawable.detail6),
            contentDescription = "주소")
        Image(
            painter = painterResource(id = R.drawable.detail7),
            contentDescription = "성과리포트")
    }
}
