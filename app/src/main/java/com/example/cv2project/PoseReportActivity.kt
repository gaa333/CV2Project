package com.example.cv2project

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cv2project.ui.theme.CV2ProjectTheme

class PoseReportActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CV2ProjectTheme {
                PoseReportScreen()
            }
        }
    }
}

@Composable
fun PoseReportScreen() {
    val context = LocalContext.current as? Activity
    var videoUri by remember { mutableStateOf<Uri?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(androidx.compose.ui.graphics.Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 15.dp, top = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "뒤로가기",
                modifier = Modifier
                    .size(25.dp)
                    .clickable { context?.finish() },
                tint = androidx.compose.ui.graphics.Color.White
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            "AI 자세 분석",
            fontSize = 25.sp,
            color = androidx.compose.ui.graphics.Color.White
        )
        Text(
            "학생 이름",
            fontSize = 18.sp,
            color = androidx.compose.ui.graphics.Color.Green
        )
        Spacer(modifier = Modifier.height(10.dp))

        Box( // 이미지 들어갈 곳
            modifier = Modifier
                .height(400.dp)
                .width(350.dp)
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                .background(androidx.compose.ui.graphics.Color.Gray)
        ) {
            videoUri?.let {
                VideoPlayer(uri = it)
            }
        }
        Spacer(modifier = Modifier.height(30.dp))

        // 유사도, 관절 결과
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "유사도 분석 결과",
                    fontSize = 18.sp,
                    color = androidx.compose.ui.graphics.Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .background(androidx.compose.ui.graphics.Color.DarkGray, shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)) // 다크그레이 박스
                        .padding(8.dp)
                ) {
                    Text(
                        text = "정확도",
                        fontSize = 18.sp,
                        color = androidx.compose.ui.graphics.Color.Green
                    )
                }
            }
            Spacer(modifier = Modifier.height(15.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "관절 각도 수치",
                    fontSize = 18.sp,
                    color = androidx.compose.ui.graphics.Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .background(androidx.compose.ui.graphics.Color.DarkGray, shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)) // 다크그레이 박스
                        .padding(8.dp)
                ) {
                    Text(
                        text = "각도",
                        fontSize = 18.sp,
                        color = androidx.compose.ui.graphics.Color.Green
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                context?.let {
                    val intent = Intent(it, PoseReportActivity::class.java)
                    it.startActivity(intent)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = androidx.compose.ui.graphics.Color.DarkGray,
                contentColor = androidx.compose.ui.graphics.Color.White
            )
        ) {
            Text(
                text = "저장",
                fontSize = 18.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}
