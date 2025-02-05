package com.example.cv2project

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.cv2project.ui.theme.CV2ProjectTheme
import androidx.camera.core.Preview



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

@SuppressLint("CheckResult")
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // 카메라 화면 표시 여부
    var showCamera by remember { mutableStateOf(false) }
    var recording: Recording? by remember { mutableStateOf(null) }
    var isRecording by remember { mutableStateOf(false) }
//    var videoCapture: VideoCapture<*>? by remember { mutableStateOf(null) }
//    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

    if (!showCamera) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                horizontalArrangement = Arrangement.Start
            ) {
                Text("싸커노트", color = Color.Black, fontSize = 30.sp)
            }
            Spacer(modifier = Modifier.size(20.dp))
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .clip(RoundedCornerShape(15.dp))
                    .background(color = Color.White)
            ) {
                Text("우리 기관 메뉴", fontSize = 15.sp)
                Spacer(modifier = Modifier.size(10.dp))
                // 첫번째 줄 버튼
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    // 알림장 버튼 notice
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_launcher_foreground),
                            contentDescription = null,
                            Modifier
                                .size(30.dp)
                                .clickable {
                                    val intent1 = Intent(context, NoticeActivity::class.java)
                                    context.startActivity(intent1)
                                }
                        )
                        Spacer(modifier = Modifier.size(5.dp))
                        Text("알림장")
                    }
                    // 공지사항 버튼 announcement
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_launcher_foreground),
                            contentDescription = null,
                            Modifier
                                .size(30.dp)
                                .clickable {
                                    val intent2 = Intent(context, AnnouncementActivity::class.java)
                                    context.startActivity(intent2)
                                }
                        )
                        Spacer(modifier = Modifier.size(5.dp))
                        Text("공지사항")
                    }
                    // 일정표 버튼 schedule
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_launcher_foreground),
                            contentDescription = null,
                            Modifier
                                .size(30.dp)
                                .clickable {
                                    val intent3 = Intent(context, ScheduleActivity::class.java)
                                    context.startActivity(intent3)
                                }
                        )
                        Spacer(modifier = Modifier.size(5.dp))
                        Text("일정표")
                    }
                }
                Spacer(modifier = Modifier.size(10.dp))
                // 두번째 줄 버튼
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    // 출석부 버튼 attendance
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_launcher_foreground),
                            contentDescription = null,
                            Modifier
                                .size(30.dp)
                                .clickable {
                                    val intent4 = Intent(context, AttendanceActivity::class.java)
                                    context.startActivity(intent4)
                                }
                        )
                        Spacer(modifier = Modifier.size(5.dp))
                        Text("출석부")
                    }
                    // 픽업 서비스 버튼 pickup service
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_launcher_foreground),
                            contentDescription = null,
                            Modifier
                                .size(30.dp)
                                .clickable {
                                    val intent5 = Intent(context, PickupServiceActivity::class.java)
                                    context.startActivity(intent5)
                                }
                        )
                        Spacer(modifier = Modifier.size(5.dp))
                        Text("픽업 서비스")
                    }
                    // 자세 분석 버튼 pose analysis
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_launcher_foreground),
                            contentDescription = null,
                            Modifier
                                .size(30.dp)
                                .clickable {
                                    val intent6 = Intent(context, PoseAnalysisActivity::class.java)
                                    context.startActivity(intent6)
                                }
                        )
                        Spacer(modifier = Modifier.size(5.dp))
                        Text("자세 분석")
                    }
                }
                Spacer(modifier = Modifier.size(10.dp))
                // 세번째 줄 버튼
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    // 성과 보고서 버튼 performance report
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_launcher_foreground),
                            contentDescription = null,
                            Modifier
                                .size(30.dp)
                                .clickable {
                                    val intent7 =
                                        Intent(context, PerformanceReportActivity::class.java)
                                    context.startActivity(intent7)
                                }
                        )
                        Spacer(modifier = Modifier.size(5.dp))
                        Text("성과 보고서")
                    }
                    // 원비 결제 버튼 payment
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_launcher_foreground),
                            contentDescription = null,
                            Modifier
                                .size(30.dp)
                                .clickable {
                                    val intent8 = Intent(context, PaymentActivity::class.java)
                                    context.startActivity(intent8)
                                }
                        )
                        Spacer(modifier = Modifier.size(5.dp))
                        Text("원비 결제")
                    }
                }
            }
            Spacer(modifier = Modifier.size(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                // 카메라 버튼
                Image(
                    painter = painterResource(R.drawable.ic_launcher_background),
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .clickable { showCamera = true }
                )
            }
        }
    } else {
        // 📸 카메라 화면
        Box(modifier = Modifier.fillMaxSize()) {
            val previewView = remember { PreviewView(context) }

            val videoCapture = remember {
                VideoCapture.withOutput(
                    Recorder.Builder()
                        .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
                        .build()
                )
            }

            val imageCapture = remember {
                ImageCapture.Builder().build()
            }

            LaunchedEffect(Unit) {
                val cameraProvider = ProcessCameraProvider.getInstance(context).get()

                val preview = Preview.Builder().build().apply {
                    setSurfaceProvider(previewView.surfaceProvider)
                }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        videoCapture,
                        imageCapture
                    )
                } catch (e: Exception) {
                    Log.e("CameraScreen", "카메라 바인딩 실패", e)
                }
            }

            // 📷 카메라 미리보기
            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize()
            )

            // 버튼 UI (사진 촬영 & 비디오 녹화)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // 📸 사진 촬영 버튼
                Image(
                    painter = painterResource(R.drawable.ic_launcher_background),
                    contentDescription = "사진 촬영",
                    modifier = Modifier
                        .size(80.dp)
                        .clickable {
                            imageCapture?.let { capture ->
                                val imageContentValues = ContentValues().apply {
                                    put(MediaStore.MediaColumns.DISPLAY_NAME, "IMG_${System.currentTimeMillis()}")
                                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MyApp")
                                    }
                                }

                                val outputOptions = ImageCapture.OutputFileOptions.Builder(
                                    context.contentResolver,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    imageContentValues
                                ).build()

                                capture.takePicture(
                                    outputOptions,
                                    ContextCompat.getMainExecutor(context),
                                    object : ImageCapture.OnImageSavedCallback {
                                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                            Log.d("CameraScreen", "Photo saved: ${outputFileResults.savedUri}")
                                        }

                                        override fun onError(exception: ImageCaptureException) {
                                            Log.e("CameraScreen", "Photo capture failed: ${exception.message}", exception)
                                        }
                                    }
                                )
                            }
                        }
                )

                // 🎥 비디오 녹화 버튼
                Image(
                    painter = painterResource(id = if (isRecording) R.drawable.ic_launcher_foreground else R.drawable.ic_launcher_foreground),
                    contentDescription = "비디오 녹화",
                    modifier = Modifier
                        .size(80.dp)
                        .clickable {
                            videoCapture?.let { videoCap ->
                                if (recording == null) { // 녹화 시작
                                    val videoContentValues = ContentValues().apply {
                                        put(MediaStore.MediaColumns.DISPLAY_NAME, "VID_${System.currentTimeMillis()}")
                                        put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                            put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/MyApp")
                                        }
                                    }

                                    val mediaStoreOutputOptions = MediaStoreOutputOptions.Builder(
                                        context.contentResolver,
                                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                                    ).setContentValues(videoContentValues).build()

                                    recording = videoCap.output.prepareRecording(context, mediaStoreOutputOptions)
                                        .start(ContextCompat.getMainExecutor(context)) { recordEvent ->
                                            when (recordEvent) {
                                                is VideoRecordEvent.Start -> {
                                                    isRecording = true
                                                    Log.d("CameraScreen", "Video recording started")
                                                }
                                                is VideoRecordEvent.Finalize -> {
                                                    if (!recordEvent.hasError()) {
                                                        Log.d("CameraScreen", "Video saved: ${recordEvent.outputResults.outputUri}")
                                                    } else {
                                                        Log.e("CameraScreen", "Video recording error: ${recordEvent.error}")
                                                    }
                                                    isRecording = false
                                                    recording = null
                                                }
                                            }
                                        }
                                } else { // 녹화 중이면 중지
                                    recording?.stop()
                                    isRecording = false
                                    recording = null
                                }
                            }
                        }
                )
            }
        }
    }
}