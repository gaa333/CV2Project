package com.example.cv2project

import android.app.Activity
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cv2project.ui.theme.CV2ProjectTheme

class DetailPerformanceReportActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val name = intent.getStringExtra("name") ?: "Unknown"
        val age = intent.getIntExtra("age", 0)
        setContent {
            CV2ProjectTheme {
                DetailPerformanceReportScreen(name, age)
            }
        }
    }
}

@Composable
fun DetailPerformanceReportScreen(name: String, age: Int) {
    val context = LocalContext.current as? Activity

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(color = Color.Black),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Absolute.SpaceBetween
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "뒤로가기",
                modifier = Modifier
                    .padding(start = 15.dp)
                    .size(25.dp)
                    .clickable { context?.finish() },
                tint = Color.White
            )
            Text(
                name,
                color = Color.White,
                fontSize = 25.sp
            )

            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "",
                modifier = Modifier
                    .padding(end = 15.dp)
                    .size(25.dp)
                    .clickable { },
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .height(600.dp)
            ) {
                Text("2025년 02월 1일", fontSize = 17.sp, modifier = Modifier.padding(top = 50.dp))
                Image(
                    painter = painterResource(R.drawable.poseresult0),
                    contentDescription = null,
                    Modifier.size(350.dp)
                )

                Text("2025년 02월 11일", fontSize = 17.sp, modifier = Modifier.padding(bottom = 280.dp).align(Alignment.BottomStart))
                Image(
                    painter = painterResource(R.drawable.poseresult),
                    contentDescription = null,
                    Modifier.size(350.dp)
                        .align(Alignment.BottomCenter)
                )
            }
            Text("보고서", fontSize = 25.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 5.dp))
            Spacer(modifier = Modifier.height(20.dp))
            Text("2월 1일에 비해 슈팅 자세 개선되었음~!", modifier = Modifier.padding(start = 10.dp))
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}