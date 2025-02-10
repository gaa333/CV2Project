package com.example.cv2project

import android.app.Activity
import android.graphics.BitmapFactory
import android.net.Uri
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
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cv2project.ui.theme.CV2ProjectTheme

class PoseReportActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val imagePath = intent.getStringExtra("imagePath")
        val hipAngle = intent.getDoubleExtra("hipAngle", 0.0)
        val kneeAngle = intent.getDoubleExtra("kneeAngle", 0.0)
        val ankleAngle = intent.getDoubleExtra("ankleAngle", 0.0)
        val hipScore = intent.getDoubleExtra("hipScore", 0.0)
        val kneeScore = intent.getDoubleExtra("kneeScore", 0.0)
        val ankleScore = intent.getDoubleExtra("ankleScore", 0.0)

        setContent {
            CV2ProjectTheme {
                PoseReportScreen(
                    imagePath,
                    hipAngle,
                    kneeAngle,
                    ankleAngle,
                    hipScore,
                    kneeScore,
                    ankleScore
                )
            }
        }
    }
}

@Composable
fun PoseReportScreen(
    imagePath: String?,
    hipAngle: Double,
    kneeAngle: Double,
    ankleAngle: Double,
    hipScore: Double,
    kneeScore: Double,
    ankleScore: Double
) {
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
            imagePath?.let {
                val bitmap = BitmapFactory.decodeFile(it)
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Analyzed Image",
                    modifier = Modifier.size(400.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Row {
            Text("Hip Angle: ${hipAngle.format(1)}")
            Spacer(modifier = Modifier.width(10.dp))
            Text("Hip Score: ${hipScore.format(1)}") // 150
        }
        Row {
            Text("Knee Angle: ${kneeAngle.format(1)}")
            Spacer(modifier = Modifier.width(10.dp))
            Text("Knee Score: ${kneeScore.format(1)}") // 105
        }
        Row {
            Text("Ankle Angle: ${ankleAngle.format(1)}")
            Spacer(modifier = Modifier.width(10.dp))
            Text("Ankle Score: ${ankleScore.format(1)}") // 180
        }
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
                        .background(
                            androidx.compose.ui.graphics.Color.DarkGray,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                        ) // 다크그레이 박스
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
                        .background(
                            androidx.compose.ui.graphics.Color.DarkGray,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                        ) // 다크그레이 박스
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
                // 성과 보고서?
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
        Spacer(modifier = Modifier.height(10.dp))
    }
}

fun Double.format(digits: Int) = "%.${digits}f".format(this)