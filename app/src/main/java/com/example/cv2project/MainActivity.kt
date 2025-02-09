package com.example.cv2project

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cv2project.ui.theme.CV2ProjectTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.systemBars
import androidx.compose.ui.res.colorResource
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CV2ProjectTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.systemBars.asPaddingValues())
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorResource(R.color.teal_700))
                .padding(bottom = 20.dp, start = 20.dp, end = 20.dp, top = 20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("앱이름", color = Color.White, fontSize = 30.sp)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f)
                .background(Color.Black)
                .padding(10.dp)
        ) {
            Text("코치님,", fontSize = 40.sp, color = Color.White, modifier = Modifier.padding(start = 10.dp))
            Text("안녕하세요 :)", fontSize = 40.sp, color = Color.White, modifier = Modifier.padding(start = 10.dp))
            Text("무엇을 도와드릴까요?", fontSize = 20.sp, color = Color.Green, modifier = Modifier.padding(start = 10.dp))
            Spacer(modifier = Modifier.weight(0.1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MenuButton("알림장", R.drawable.notice, context, NoticeActivity::class.java)
                MenuButton("공지사항", R.drawable.announcement, context, AnnouncementActivity::class.java)
                MenuButton("일정표", R.drawable.schedule, context, ScheduleActivity::class.java)
            }
            Spacer(modifier = Modifier.weight(0.1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MenuButton("픽업 서비스", R.drawable.pickup, context, PickupServiceActivity::class.java)
                MenuButton("원비 결제", R.drawable.pay, context, PaymentActivity::class.java)
                MenuButton("학생 관리", R.drawable.student, context, StudentClassListActivity::class.java)
            }
            Spacer(modifier = Modifier.weight(0.1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MenuButton("성과 보고서", R.drawable.report, context, PerformanceReportActivity::class.java)
                MenuButton("자세 분석", R.drawable.pose, context, PoseAnalysisActivity::class.java)
            }
            Spacer(modifier = Modifier.weight(0.1f))
        }
    }
}

@Composable
fun MenuButton(title: String, imageResId: Int, context: Context, activity: Class<*>) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (pressed) 0.9f else 1f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        tryAwaitRelease()
                        pressed = false
                        val intent = Intent(context, activity)
                        context.startActivity(intent)
                    }
                )
            }
            .scale(scale)
            .padding(8.dp)
            .clip(RoundedCornerShape(12.dp)) // 모서리 둥글게
    ) {
        Box(
            modifier = Modifier
                .size(80.dp) // 원 크기
                .clip(CircleShape) // 원형으로 클리핑
                .background(Color.DarkGray) // 배경 색 흰색
        ) {
            Image(
                painter = painterResource(imageResId),
                contentDescription = title,
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.Center)
                    .fillMaxSize() // 이미지가 원을 채우게 함
            )
        }
        Spacer(modifier = Modifier.size(5.dp))
        Text(text = title, color = Color.White)
    }
}