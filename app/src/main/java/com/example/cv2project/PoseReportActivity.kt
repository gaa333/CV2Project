package com.example.cv2project

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cv2project.ui.theme.CV2ProjectTheme
import java.io.OutputStream

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
        Spacer(modifier = Modifier.height(40.dp))
        // 이미지 들어갈 곳
        Box(
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp)
                .height(240.dp)
                .width(350.dp)
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                .background(androidx.compose.ui.graphics.Color.Gray),
        ) {
            imagePath?.let {
                val bitmap = BitmapFactory.decodeFile(it)
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Analyzed Image",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Spacer(modifier = Modifier.height(60.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier.fillMaxHeight()
                    .fillMaxWidth(0.25f)
            ) {
                Text("", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.size(10.dp))
                Text("Hip Angle", color = Color.White)
                Spacer(modifier = Modifier.size(5.dp))
                Text("Knee Angle", color = Color.White)
                Spacer(modifier = Modifier.size(5.dp))
                Text("Ankle Angle", color = Color.White)
            }
            Spacer(modifier = Modifier.size(10.dp))
            Column(
                modifier = Modifier.fillMaxHeight()
                    .fillMaxWidth(0.3f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) { Text("적정 각도", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.size(10.dp))
                Text("150 도", color = Color.White)
                Spacer(modifier = Modifier.size(5.dp))
                Text("105 도", color = Color.White)
                Spacer(modifier = Modifier.size(5.dp))
                Text("180 도", color = Color.White)
            }
            Spacer(modifier = Modifier.size(15.dp))
            Column(
                modifier = Modifier.fillMaxHeight()
                    .fillMaxWidth(0.45f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("측정 각도", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.size(10.dp))
                Text("${hipAngle.format(1)} 도", color = Color.White)
                Spacer(modifier = Modifier.size(5.dp))
                Text("${kneeAngle.format(1)} 도", color = Color.White)
                Spacer(modifier = Modifier.size(5.dp))
                Text("${ankleAngle.format(1)} 도", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                // 갤러리 저장
                imagePath?.let {
                    val bitmap = BitmapFactory.decodeFile(it)
                    saveImageToGallery(context!!, bitmap) // 갤러리에 저장
                    Toast.makeText(context, "이미지가 갤러리에 저장되었습니다!", Toast.LENGTH_SHORT).show()
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
        Spacer(modifier = Modifier.height(10.dp))
    }
}

fun Double.format(digits: Int) = "%.${digits}f".format(this)

fun saveImageToGallery(context: Context, bitmap: Bitmap, filename: String = "PoseImage") {
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "$filename.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/PoseReports")
    }

    val contentResolver = context.contentResolver
    val imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    imageUri?.let { uri ->
        val outputStream: OutputStream? = contentResolver.openOutputStream(uri)
        outputStream?.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) // JPEG 형식으로 저장
        }
    }
}