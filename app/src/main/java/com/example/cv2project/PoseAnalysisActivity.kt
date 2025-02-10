@file:OptIn(ExperimentalAnimationApi::class)

package com.example.cv2project

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.cv2project.ui.theme.CV2ProjectTheme
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.Executors
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import androidx.activity.viewModels
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.delay
import java.io.FileOutputStream
import kotlin.math.max
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class PoseAnalysisActivity : ComponentActivity(), PoseLandmarkerHelper.LandmarkerListener {

    private val viewModel: PoseAnalysisViewModel by viewModels()
    private var poseLandmarkerHelper: PoseLandmarkerHelper? = null
    private val executorService = Executors.newSingleThreadExecutor()
    private var selectedVideoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CV2ProjectTheme {
                val frames by viewModel.frames.collectAsState(initial = emptyList())
                PoseAnalysisScreen(
                    onSelectVideo = { openGallery(viewModel) },
                    frames = frames,
                    selectedVideoUri
                )
            }
        }
    }

    private fun openGallery(viewModel: PoseAnalysisViewModel) {
        val intent = Intent(Intent.ACTION_PICK).apply { type = "video/*" }
        videoPickerLauncher.launch(intent)
    }

    private val videoPickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.data?.let { uri ->
                    selectedVideoUri = uri
                    processVideo(uri)
                }
            }
        }

    private fun processVideo(videoUri: Uri) {
        val retriever = MediaMetadataRetriever()
        executorService.execute {
            try {
                retriever.setDataSource(this@PoseAnalysisActivity, videoUri)
                val videoLengthMs =
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                        ?.toLong() ?: 0
                val inferenceIntervalMs = 100L
                val numFrames = (videoLengthMs / inferenceIntervalMs).toInt()

                poseLandmarkerHelper = PoseLandmarkerHelper(
                    context = this@PoseAnalysisActivity,
                    runningMode = RunningMode.IMAGE,
                    currentModel = PoseLandmarkerHelper.MODEL_POSE_LANDMARKER_LITE,
                    poseLandmarkerHelperListener = this@PoseAnalysisActivity
                )

                val frameList = mutableListOf<Bitmap>()
                var lastTimestampMs = -1L

                for (i in 0 until numFrames) {
                    val timestampMs = i * inferenceIntervalMs * 1000L
                    val validTimestampMs = if (lastTimestampMs == -1L) timestampMs else max(
                        timestampMs,
                        lastTimestampMs + inferenceIntervalMs * 1000L
                    )

                    val frame = retriever.getFrameAtTime(
                        validTimestampMs,
                        MediaMetadataRetriever.OPTION_CLOSEST
                    )

                    if (frame == null) {
                        Log.e("PoseAnalysis", "❌ Frame at timestamp $validTimestampMs is null")
                        continue
                    } else {
                        Log.d("PoseAnalysis", "✅ Successfully retrieved frame at $validTimestampMs")
                    }

                    val argb8888Frame = frame.copy(Bitmap.Config.ARGB_8888, true)

                    // ✅ Pose 추정 후 새로운 이미지 저장
                    val result = poseLandmarkerHelper?.detectImage(argb8888Frame)
                    result?.results?.firstOrNull()?.let { poseResult ->
                        val processedBitmap = drawPoseOnImage(argb8888Frame, poseResult)
                        frameList.add(processedBitmap)

                        // ✅ 저장된 프레임 로그 출력
                        val savedPath = saveBitmap(processedBitmap, "frame_$i.png")
                        Log.d("PoseAnalysis", "✅ Frame $i saved at: $savedPath")
                    }

                    lastTimestampMs = validTimestampMs
                }

                runOnUiThread {
                    viewModel.setFrames(frameList)
                    Log.d("PoseAnalysisViewModel", "✅ Frames sent to ViewModel: ${frameList.size}")
                }

            } catch (e: Exception) {
                Log.e("PoseAnalysis", "❌ Video processing failed: ${e.message}", e)
            } finally {
                retriever.release()
                poseLandmarkerHelper?.clearPoseLandmarker()
            }
        }
    }

    private fun saveBitmap(bitmap: Bitmap, filename: String): String {
        val directory = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "PoseFrames")
        if (!directory.exists()) directory.mkdirs()

        val file = File(directory, filename)
        try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
        } catch (e: Exception) {
            Log.e("PoseAnalysis", "❌ Failed to save image: ${e.message}")
        }
        return file.absolutePath
    }


    private fun drawPoseOnImage(bitmap: Bitmap, result: PoseLandmarkerResult): Bitmap {
        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)
        val paint = Paint().apply {
            color = Color.RED
            strokeWidth = 10f
            style = Paint.Style.FILL
        }

        val linePaint = Paint().apply {
            color = Color.WHITE
            strokeWidth = 5f
            style = Paint.Style.STROKE
        }

        val connections = listOf(
            Pair(11, 13), Pair(13, 15),
            Pair(12, 14), Pair(14, 16),
            Pair(11, 12),
            Pair(11, 23), Pair(12, 24),
            Pair(23, 25), Pair(25, 27),
            Pair(24, 26), Pair(26, 28),
            Pair(27, 29), Pair(29, 31),
            Pair(28, 30), Pair(30, 32)
        )

        result.landmarks().forEach { landmark ->
            landmark.forEach { point ->
                canvas.drawPoint(
                    point.x() * mutableBitmap.width,
                    point.y() * mutableBitmap.height,
                    paint
                )
            }

            for ((startIdx, endIdx) in connections) {
                val start = landmark[startIdx]
                val end = landmark[endIdx]
                canvas.drawLine(
                    start.x() * mutableBitmap.width, start.y() * mutableBitmap.height,
                    end.x() * mutableBitmap.width, end.y() * mutableBitmap.height,
                    linePaint
                )
            }
        }
        return mutableBitmap
    }

    //
    override fun onError(error: String, errorCode: Int) {
        Log.e("PoseAnalysis", "Error: $error (Code: $errorCode)")
        runOnUiThread {
            Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResults(resultBundle: PoseLandmarkerHelper.ResultBundle) {
        Log.d("PoseAnalysis", "Pose detected: ${resultBundle.results.size}")
    }

}


@Composable
fun PoseAnalysisScreen(onSelectVideo: () -> Unit, frames: List<Bitmap>, selectedVideoUri: Uri?) {

//    var selectedVideoUri by remember { mutableStateOf<Uri?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(120, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.45.238:5000/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service = retrofit.create(FlaskService::class.java)


    val context = LocalContext.current as? Activity
    var currentFrameIndex by remember { mutableStateOf(0) }

    // 카메라 화면 표시 여부
    var showCamera by remember { mutableStateOf(false) }
    var recording: Recording? by remember { mutableStateOf(null) }
    var isRecording by remember { mutableStateOf(false) }
    RequestCameraPermission()

    LaunchedEffect(frames) {
        while (frames.isNotEmpty()) {
            delay(500L) // Control the frame rate
            currentFrameIndex = (currentFrameIndex + 1) % frames.size
            Log.d("FrameUpdate", "✅ Current frame index updated: $currentFrameIndex") // 로그 추가
        }
    }

    if (!showCamera) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = androidx.compose.ui.graphics.Color.Black),
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
                "실시간 동작 분석",
                fontSize = 25.sp,
                color = androidx.compose.ui.graphics.Color.White
            )
            Text(
                "학생 영상을 넣어보세요",
                fontSize = 18.sp,
                color = androidx.compose.ui.graphics.Color.Green
            )
            Spacer(modifier = Modifier.height(30.dp))

            Box( // 영상 들어갈 곳
                modifier = Modifier
                    .height(400.dp)
                    .width(350.dp)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                    .background(androidx.compose.ui.graphics.Color.Gray),
                contentAlignment = Alignment.TopCenter
            ) {

                // canvas
                if (frames.isNotEmpty()) {
                    PoseAnimation(frames)
                } else {
                    Text("no frames")
                }
            }
            Spacer(modifier = Modifier.height(30.dp))

            // 카메라, 갤러리 버튼
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        showCamera = true
                    },
                    modifier = Modifier
                        .width(50.dp)
                        .height(50.dp)
                        .background(
                            androidx.compose.ui.graphics.Color.DarkGray,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                        )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.camera),
                        contentDescription = "Camera Icon"
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        onSelectVideo()
                    },
                    modifier = Modifier
                        .width(50.dp)
                        .height(50.dp)
                        .background(
                            androidx.compose.ui.graphics.Color.DarkGray,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                        )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.gallery),
                        contentDescription = "Gallery Icon"
                    )
                }
            }
            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    selectedVideoUri?.let { uri ->
                        coroutineScope.launch {
                            try {
                                val file = withContext(Dispatchers.IO) {
                                    File(context?.cacheDir, "temp_video").apply {
                                        context?.contentResolver?.openInputStream(uri)
                                            ?.use { input ->
                                                outputStream().use { output ->
                                                    input.copyTo(output)
                                                }
                                            }
                                    }
                                }

                                val requestFile = file.asRequestBody("video/*".toMediaTypeOrNull())
                                val body = MultipartBody.Part.createFormData(
                                    "video",
                                    file.name,
                                    requestFile
                                )

                                val serverResponse = withContext(Dispatchers.IO) {
                                    service.uploadVideo(body)
                                }

                                serverResponse.results.firstOrNull()?.let { result ->
                                    val imageFile =
                                        context?.let { saveImageToFile(it, result.image_data) }
                                    val intent =
                                        Intent(context, PoseReportActivity::class.java).apply {
                                            putExtra("imagePath", imageFile?.absolutePath)
                                            putExtra("hipAngle", result.hip_angle)
                                            putExtra("kneeAngle", result.knee_angle)
                                            putExtra("ankleAngle", result.ankle_angle)
                                            putExtra("hipScore", result.hip_score)
                                            putExtra("kneeScore", result.knee_score)
                                            putExtra("ankleScore", result.ankle_score)
                                        }
                                    context?.startActivity(intent)
                                }
                            } catch (e: Exception) {
                                println("Error: ${e.message}")
                            }
                        }
                    } ?: println("No video selected") // `selectedVideoUri`가 null일 경우 로그 출력
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
                    text = "AI 자세 분석",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
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
                previewView.scaleType = PreviewView.ScaleType.FIT_END // 또는 FILL_START
            }

            LaunchedEffect(Unit) {
                val cameraProvider = ProcessCameraProvider.getInstance(context).get()

                val preview = Preview.Builder()
//                    .setTargetAspectRatio(getAspectRatio(previewView.width, previewView.height))
                    .build().apply {
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
                        .background(androidx.compose.ui.graphics.Color.White),
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
                        .weight(0.75f)
                )

                // 🔽 아래쪽 white 바 (사진 촬영 및 비디오 녹화 버튼 포함)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.15f) // 전체 화면의 15% 차지
                        .background(androidx.compose.ui.graphics.Color.White),
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
fun PoseAnimation(frames: List<Bitmap>) {
    var currentFrameIndex by remember { mutableStateOf(0) }

    // 일정한 간격으로 프레임을 업데이트
    LaunchedEffect(frames) {
        while (true) {
            delay(100L) // 100ms마다 프레임 변경 (FPS 10)
            currentFrameIndex = (currentFrameIndex + 1) % frames.size
        }
    }

    Canvas(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (frames.isNotEmpty()) {
            val frame = frames[currentFrameIndex]
            drawImage(frame.asImageBitmap())
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

fun saveImageToFile(context: android.content.Context, base64Image: String): File {
    val imageBytes = Base64.decode(base64Image, Base64.DEFAULT)
    val file = File(context.cacheDir, "temp_image.jpg")
    file.writeBytes(imageBytes)
    return file
}