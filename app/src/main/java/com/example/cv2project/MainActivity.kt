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
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.statusBars
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
            .padding(WindowInsets.statusBars.only(WindowInsetsSides.Top).asPaddingValues())
            .background(Color.Black),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(id = R.drawable.nextgoal),
                contentDescription = "앱로고"
            )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f)
                .background(Color.Unspecified)
                .padding(10.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.hi),
                contentDescription = "코치 하이",
                modifier = Modifier
                    .height(150.dp)
                    .align(Alignment.Start)
                    .padding(start = 10.dp, top = 10.dp)
            )
            Spacer(modifier = Modifier.weight(0.05f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MenuButton(R.drawable.notice, context, NoticeActivity::class.java) //알림장
                MenuButton(R.drawable.announcement, context, AnnouncementActivity::class.java) //공지사항
                MenuButton( R.drawable.schedule, context, ScheduleActivity::class.java) //일정표
            }
            Spacer(modifier = Modifier.weight(0.1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MenuButton( R.drawable.pickup, context, PickupServiceActivity::class.java) //픽업서비스
                MenuButton( R.drawable.pay, context, PaymentActivity::class.java) //원비결제
                MenuButton( R.drawable.student, context, StudentClassListActivity::class.java) //학생관리
            }
            Spacer(modifier = Modifier.weight(0.1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MenuButton(R.drawable.report, context, PerformanceReportActivity::class.java) //성과보고서
                MenuButton(R.drawable.pose, context, PoseAnalysisActivity::class.java) //자세분석
            }
            Spacer(modifier = Modifier.weight(0.1f))
        }
    }
}

@Composable
fun MenuButton(imageResId: Int, context: Context, activity: Class<*>) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (pressed) 0.9f else 1f)

    Image(
        painter = painterResource(imageResId),
        contentDescription = "메뉴 버튼",
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
            .size(100.dp) // 이미지 크기 조정
    )
}
