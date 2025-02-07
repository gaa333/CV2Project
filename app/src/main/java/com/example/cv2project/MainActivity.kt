package com.example.cv2project

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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

    RequestCameraPermission()

    if (!showCamera) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.1f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.1f),
                horizontalArrangement = Arrangement.Start
            ) {
                Text("싸커노트", color = Color.Black, fontSize = 30.sp)
            }

            Spacer(modifier = Modifier.weight(0.1f))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.6f)
                    .clip(RoundedCornerShape(15.dp))
                    .background(Color.White)
                    .padding(10.dp)
            ) {
                Text("우리 기관 메뉴", fontSize = 20.sp, modifier = Modifier.padding(start = 10.dp))
                Spacer(modifier = Modifier.weight(0.1f))

                // 첫 번째 줄 버튼 (각 메뉴마다 다른 이미지 적용)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    MenuButton("알림장", R.drawable.red, context, NoticeActivity::class.java)
                    MenuButton("공지사항", R.drawable.red, context, AnnouncementActivity::class.java)
                    MenuButton("일정표", R.drawable.red, context, ScheduleActivity::class.java)
                }

                Spacer(modifier = Modifier.weight(0.1f))

                // 두 번째 줄 버튼
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    MenuButton("출석부", R.drawable.red, context, AttendanceActivity::class.java)
                    MenuButton("픽업 서비스", R.drawable.red, context, PickupServiceActivity::class.java)
                    MenuButton("자세 분석", R.drawable.red, context, PoseAnalysisActivity::class.java)
                }

                Spacer(modifier = Modifier.weight(0.1f))

                // 세 번째 줄 버튼
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    MenuButton("성과 보고서", R.drawable.red, context, PerformanceReportActivity::class.java)
                    MenuButton("원비 결제", R.drawable.red, context, PaymentActivity::class.java)
                    MenuButton("학생 관리", R.drawable.red, context, StudentClassListActivity::class.java)
                }
            }

            Spacer(modifier = Modifier.weight(0.1f))


            // 카메라 실행 버튼
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.3f),
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.camera),
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .clickable { showCamera = true }
                )
            }
            Spacer(modifier = Modifier.weight(0.1f))
        }
    } else {
        // 📸 카메라 화면
        Box(modifier = Modifier.fillMaxSize()) {
            val context = LocalContext.current
            val lifecycleOwner = LocalLifecycleOwner.current
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

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // 🔼 위쪽 white 바 (뒤로 가기 버튼 포함)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.1f) // 전체 화면의 10% 차지
                        .background(Color.White),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Image(
                        painter = painterResource(R.drawable.x), // 뒤로가기 버튼 아이콘
                        contentDescription = "뒤로가기",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(start = 20.dp, bottom = 10.dp)
                            .clickable {
                                // 뒤로 가기 기능
                                showCamera = false
                            }
                    )
                }

                // 📷 카메라 미리보기
                AndroidView(
                    factory = { previewView },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.75f) // 전체 화면의 75% 차지
                )

                // 🔽 아래쪽 white 바 (사진 촬영 및 비디오 녹화 버튼 포함)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.15f) // 전체 화면의 15% 차지
                        .background(Color.White),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Top
                    ) {
                        // 📸 사진 촬영 버튼
                        Image(
                            painter = painterResource(R.drawable.camera),
                            contentDescription = "사진 촬영",
                            modifier = Modifier
                                .size(50.dp)
                                .clickable {
                                    imageCapture?.let { capture ->
                                        val imageContentValues = ContentValues().apply {
                                            put(
                                                MediaStore.MediaColumns.DISPLAY_NAME,
                                                "IMG_${System.currentTimeMillis()}"
                                            )
                                            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                                put(
                                                    MediaStore.Images.Media.RELATIVE_PATH,
                                                    "Pictures/MyApp"
                                                )
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
                                                    Log.d(
                                                        "CameraScreen",
                                                        "Photo saved: ${outputFileResults.savedUri}"
                                                    )
                                                }

                                                override fun onError(exception: ImageCaptureException) {
                                                    Log.e(
                                                        "CameraScreen",
                                                        "Photo capture failed: ${exception.message}",
                                                        exception
                                                    )
                                                }
                                            }
                                        )
                                    }
                                }
                        )

                        // 🎥 비디오 녹화 버튼
                        Image(
                            painter = painterResource(id = if (isRecording) R.drawable.blackrect else R.drawable.red),
                            contentDescription = "비디오 녹화",
                            modifier = Modifier
                                .size(50.dp)
                                .clickable {
                                    videoCapture?.let { videoCap ->
                                        if (recording == null) { // 녹화 시작
                                            val videoContentValues = ContentValues().apply {
                                                put(
                                                    MediaStore.MediaColumns.DISPLAY_NAME,
                                                    "VID_${System.currentTimeMillis()}"
                                                )
                                                put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                                    put(
                                                        MediaStore.Video.Media.RELATIVE_PATH,
                                                        "Movies/MyApp"
                                                    )
                                                }
                                            }

                                            val mediaStoreOutputOptions =
                                                MediaStoreOutputOptions.Builder(
                                                    context.contentResolver,
                                                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                                                ).setContentValues(videoContentValues).build()

                                            recording = videoCap.output.prepareRecording(
                                                context,
                                                mediaStoreOutputOptions
                                            )
                                                .start(ContextCompat.getMainExecutor(context)) { recordEvent ->
                                                    when (recordEvent) {
                                                        is VideoRecordEvent.Start -> {
                                                            isRecording = true
                                                            Log.d(
                                                                "CameraScreen",
                                                                "Video recording started"
                                                            )
                                                        }

                                                        is VideoRecordEvent.Finalize -> {
                                                            if (!recordEvent.hasError()) {
                                                                Log.d(
                                                                    "CameraScreen",
                                                                    "Video saved: ${recordEvent.outputResults.outputUri}"
                                                                )
                                                            } else {
                                                                Log.e(
                                                                    "CameraScreen",
                                                                    "Video recording error: ${recordEvent.error}"
                                                                )
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
    }
}

@Composable
fun RequestCameraPermission() {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (!granted) {
                Toast.makeText(context, "카메라 권한이 필요합니다.", Toast.LENGTH_LONG).show()
            }
        }
    )

    LaunchedEffect(Unit) {
        launcher.launch(android.Manifest.permission.CAMERA)
    }
}

@Composable
fun MenuButton(title: String, imageResId: Int, context: Context, activity: Class<*>) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.clickable {
            val intent = Intent(context, activity)
            context.startActivity(intent)
        }
    ) {
        Image(
            painter = painterResource(imageResId), // ✅ 각 메뉴마다 다른 이미지 적용
            contentDescription = title,
            modifier = Modifier
                .size(30.dp)
        )
        Spacer(modifier = Modifier.size(5.dp))
        Text(title)
    }
}
