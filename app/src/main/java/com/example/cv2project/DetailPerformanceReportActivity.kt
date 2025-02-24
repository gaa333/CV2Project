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
import androidx.compose.foundation.layout.fillMaxHeight
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp)
            ) {
                Text("2025년 02월 1일", fontSize = 17.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 50.dp))
                Image(
                    painter = painterResource(R.drawable.poseresult0),
                    contentDescription = null,
                    Modifier.size(350.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.28f)
                    ) {
                        Text("", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.size(10.dp))
                        Text("Hip Angle", )
                        Spacer(modifier = Modifier.size(5.dp))
                        Text("Knee Angle", )
                        Spacer(modifier = Modifier.size(5.dp))
                        Text("Ankle Angle", )
                    }
//                    Spacer(modifier = Modifier.size(10.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.3f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "적정 각도",

                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        Text("150 도", )
                        Spacer(modifier = Modifier.size(5.dp))
                        Text("105 도", )
                        Spacer(modifier = Modifier.size(5.dp))
                        Text("180 도", )
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.45f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "측정 각도",

                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        Text("163.9 도", )
                        Spacer(modifier = Modifier.size(5.dp))
                        Text("105.9 도", )
                        Spacer(modifier = Modifier.size(5.dp))
                        Text("106.1 도", )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp)

            ) {
                Text(
                    "2025년 02월 11일",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 45.dp)
                )
                Image(
                    painter = painterResource(R.drawable.poseresult),
                    contentDescription = null,
                    Modifier
                        .size(350.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.28f)
                    ) {
                        Text("", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.size(10.dp))
                        Text("Hip Angle", )
                        Spacer(modifier = Modifier.size(5.dp))
                        Text("Knee Angle", )
                        Spacer(modifier = Modifier.size(5.dp))
                        Text("Ankle Angle", )
                    }
//                    Spacer(modifier = Modifier.size(10.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.3f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "적정 각도",

                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        Text("150 도", )
                        Spacer(modifier = Modifier.size(5.dp))
                        Text("105 도", )
                        Spacer(modifier = Modifier.size(5.dp))
                        Text("180 도", )
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.45f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "측정 각도",

                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        Text("126.2 도", )
                        Spacer(modifier = Modifier.size(5.dp))
                        Text("88.8 도", )
                        Spacer(modifier = Modifier.size(5.dp))
                        Text("142.6 도", )
                    }
                }

            }
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                "보고서",
                fontSize = 25.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 5.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text("2월 1일 당시 슈팅 자세에 비해 자세가 많이 개선되었습니다. ", modifier = Modifier.padding(start = 10.dp))
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}