package com.example.cv2project

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

// Pose Analysis
@Composable
fun PoseAnalysisScreen(navController: NavController, userRole: String) {
    val context = LocalContext.current
    // Compose ViewModel 사용 (기존 by viewModels() 대신)
    val viewModel: PoseAnalysisViewModel = viewModel()
    val frames by viewModel.frames.collectAsState(initial = emptyList())
    var selectedVideoUri by remember { mutableStateOf<Uri?>(null) }

    // videoPickerLauncher: 비디오 선택을 위한 ActivityResult API를 Compose 방식으로 사용
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedVideoUri = uri
                processVideo(context, uri, viewModel)
            }
        }
    }
    // onSelectVideo: 비디오 선택 인텐트 실행
    val onSelectVideo = {
        val intent = Intent(Intent.ACTION_PICK).apply { type = "video/*" }
        videoPickerLauncher.launch(intent)
    }

    // PoseAnalysisScreen UI 구성
    PoseAnalysisContent(
        navController = navController,
        onSelectVideo = onSelectVideo,
        frames = frames,
        selectedVideoUri = selectedVideoUri,
        viewModel = viewModel
    )
}

@Composable
fun PoseAnalysisContent(
    navController: NavController,
    onSelectVideo: () -> Unit,
    frames: List<Bitmap>,
    selectedVideoUri: Uri?,
    viewModel: PoseAnalysisViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    var isAnalyzing by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(frames) {
        if (frames.isNotEmpty()) {
            isLoading = false
        }
    }

    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(120, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.45.174:5000/") // Flask 서버 주소
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service = retrofit.create(FlaskService::class.java)

    val context = LocalContext.current // ✅ 현재 Composable의 Context 가져오기
    var currentFrameIndex by remember { mutableStateOf(0) }

    // 카메라 화면 표시 여부
    var showCamera by remember { mutableStateOf(false) }
    var recording: Recording? by remember { mutableStateOf(null) }
    var isRecording by remember { mutableStateOf(false) }
    RequestCameraPermission()

    LaunchedEffect(frames) {
        while (frames.isNotEmpty()) {
            delay(500L)
            currentFrameIndex = (currentFrameIndex + 1) % frames.size
            Log.d("FrameUpdate", "Current frame index updated: $currentFrameIndex")
        }
    }

    if (!showCamera) {
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
                        .size(50.dp)
                        .padding(top = 15.dp)
                        .clickable { navController.popBackStack() },
                    tint = androidx.compose.ui.graphics.Color.White
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                "실시간 동작 분석",
                fontSize = 25.sp,
                color = androidx.compose.ui.graphics.Color.White
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                "학생 영상을 넣어보세요",
                fontSize = 18.sp,
                color = androidx.compose.ui.graphics.Color.Green
            )
            Spacer(modifier = Modifier.height(80.dp))

            Box(
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp)
                    .height(240.dp)
                    .width(350.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(androidx.compose.ui.graphics.Color.Gray),
                contentAlignment = Alignment.TopCenter
            ) {
                if (isAnalyzing) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = androidx.compose.ui.graphics.Color.White,
                        )
                        Spacer(modifier = Modifier.size(15.dp))
                        Text(
                            "AI 분석 중...",
                            fontSize = 20.sp,
                            modifier = Modifier.padding(start = 5.dp)
                        )
                    }
                } else if (isLoading) {
                    CircularProgressIndicator(
                        color = androidx.compose.ui.graphics.Color.White,
                        modifier = Modifier.padding(top = 100.dp)
                    )
                } else {
                    if (frames.isNotEmpty()) {
                        PoseAnimation(frames)
                    } else {
                        Text(
                            "동작 분석 영상",
                            fontSize = 20.sp,
                            modifier = Modifier.padding(top = 100.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(100.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { showCamera = true },
                    modifier = Modifier
                        .width(60.dp)
                        .height(60.dp)
                        .background(
                            androidx.compose.ui.graphics.Color.DarkGray,
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.camera),
                        contentDescription = "Camera Icon",
                        modifier = Modifier.size(50.dp)
                    )
                }
                Spacer(modifier = Modifier.width(60.dp))
                IconButton(
                    onClick = {
                        onSelectVideo()
                        isLoading = true
                    },
                    modifier = Modifier
                        .width(60.dp)
                        .height(60.dp)
                        .background(
                            androidx.compose.ui.graphics.Color.DarkGray,
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.gallery),
                        contentDescription = "Gallery Icon",
                        modifier = Modifier.size(50.dp)
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
                                    File(context.cacheDir, "temp_video").apply { // ✅ Activity 의존 제거
                                        context.contentResolver.openInputStream(uri)?.use { input ->
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
                                        saveImageToFile(context, result.image_data) // ✅ Context 전달

                                    // ✅ Intent 대신 NavController를 사용하여 PoseReportScreen으로 이동
                                    navController.navigate(
                                        "poseReport?imagePath=${imageFile?.absolutePath}" +
                                                "&hipAngle=${result.hip_angle}" +
                                                "&kneeAngle=${result.knee_angle}" +
                                                "&ankleAngle=${result.ankle_angle}" +
                                                "&hipScore=${result.hip_score}" +
                                                "&kneeScore=${result.knee_score}" +
                                                "&ankleScore=${result.ankle_score}"
                                    )
                                }
                            } catch (e: Exception) {
                                println("Error: ${e.message}")
                            }
                        }
                    } ?: println("No video selected")
                    isAnalyzing = true
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
        // 카메라 화면
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
                previewView.scaleType = PreviewView.ScaleType.FILL_START
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
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.1f)
                        .background(androidx.compose.ui.graphics.Color.White),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Image(
                        painter = painterResource(R.drawable.x),
                        contentDescription = "뒤로가기",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(start = 20.dp, bottom = 10.dp)
                            .clickable { showCamera = false }
                    )
                }
                AndroidView(
                    factory = { previewView },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.75f)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.15f)
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
                        Image(
                            painter = painterResource(R.drawable.camera),
                            contentDescription = "사진 촬영",
                            modifier = Modifier
                                .size(50.dp)
                                .clickable {
                                    imageCapture.let { capture ->
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
                        Image(
                            painter = painterResource(id = if (isRecording) R.drawable.blackrect else R.drawable.red),
                            contentDescription = "비디오 녹화",
                            modifier = Modifier
                                .size(50.dp)
                                .clickable {
                                    videoCapture.let { videoCap ->
                                        if (recording == null) {
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
                                        } else {
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
    LaunchedEffect(frames) {
        while (true) {
            delay(100L)
            currentFrameIndex = (currentFrameIndex + 1) % frames.size
        }
    }
    Canvas(
        Modifier.fillMaxWidth()
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

fun saveImageToFile(context: Context, base64Image: String): File {
    val imageBytes = Base64.decode(base64Image, Base64.DEFAULT)
    val file = File(context.cacheDir, "temp_image.jpg")
    file.writeBytes(imageBytes)
    return file
}

// --- Helper Functions ---

fun processVideo(context: Context, videoUri: Uri, viewModel: PoseAnalysisViewModel) {
    val executorService = Executors.newSingleThreadExecutor()
    val retriever = MediaMetadataRetriever()
    executorService.execute {
        try {
            retriever.setDataSource(context, videoUri)
            val videoLengthMs =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong()
                    ?: 0
            val inferenceIntervalMs = 100L
            val numFrames = (videoLengthMs / inferenceIntervalMs).toInt()

            val poseLandmarkerHelper = PoseLandmarkerHelper(
                context = context,
                runningMode = RunningMode.IMAGE,
                currentModel = PoseLandmarkerHelper.MODEL_POSE_LANDMARKER_LITE,
                poseLandmarkerHelperListener = object : PoseLandmarkerHelper.LandmarkerListener {
                    override fun onError(error: String, errorCode: Int) {
                        Log.e("PoseAnalysis", "Error: $error (Code: $errorCode)")
                        (context as? Activity)?.runOnUiThread {
                            Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onResults(resultBundle: PoseLandmarkerHelper.ResultBundle) {
                        Log.d("PoseAnalysis", "Pose detected: ${resultBundle.results.size}")
                    }
                }
            )

            val frameList = mutableListOf<Bitmap>()
            var lastTimestampMs = -1L
            for (i in 0 until numFrames) {
                val timestampMs = i * inferenceIntervalMs * 1000L
                val validTimestampMs = if (lastTimestampMs == -1L)
                    timestampMs
                else
                    kotlin.math.max(timestampMs, lastTimestampMs + inferenceIntervalMs * 1000L)
                val frame = retriever.getFrameAtTime(
                    validTimestampMs,
                    MediaMetadataRetriever.OPTION_CLOSEST
                )
                if (frame == null) {
                    Log.e("PoseAnalysis", "Frame at timestamp $validTimestampMs is null")
                    continue
                } else {
                    Log.d("PoseAnalysis", "Successfully retrieved frame at $validTimestampMs")
                }
                val argb8888Frame = frame.copy(Bitmap.Config.ARGB_8888, true)
                val result = poseLandmarkerHelper.detectImage(argb8888Frame)
                result?.results?.firstOrNull()?.let { poseResult ->
                    val processedBitmap = drawPoseOnImage(argb8888Frame, poseResult)
                    frameList.add(processedBitmap)
                    val savedPath = saveBitmap(context, processedBitmap, "frame_$i.png")
                    Log.d("PoseAnalysis", "Frame $i saved at: $savedPath")
                }
                lastTimestampMs = validTimestampMs
            }
            (context as? Activity)?.runOnUiThread {
                viewModel.setFrames(frameList)
                Log.d("PoseAnalysisViewModel", "Frames sent to ViewModel: ${frameList.size}")
            }
        } catch (e: Exception) {
            Log.e("PoseAnalysis", "Video processing failed: ${e.message}", e)
        } finally {
            retriever.release()
        }
    }
}

fun saveBitmap(context: Context, bitmap: Bitmap, filename: String): String {
    val directory = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "PoseFrames")
    if (!directory.exists()) directory.mkdirs()
    val file = File(directory, filename)
    try {
        FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        }
    } catch (e: Exception) {
        Log.e("PoseAnalysis", "Failed to save image: ${e.message}")
    }
    return file.absolutePath
}

fun drawPoseOnImage(bitmap: Bitmap, result: PoseLandmarkerResult): Bitmap {
    val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
    val canvas = Canvas(mutableBitmap)
    val paint = Paint().apply {
        color = Color.Red.toArgb()
        strokeWidth = 10f
        style = Paint.Style.FILL
    }
    val linePaint = Paint().apply {
        color = Color.White.toArgb()
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

@Composable
fun PoseReportScreen(
    navController: NavController,
    imagePath: String?,
    hipAngle: Double,
    kneeAngle: Double,
    ankleAngle: Double,
    hipScore: Double,
    kneeScore: Double,
    ankleScore: Double,
    userRole: String
) {
    val context = LocalContext.current
    var videoUri by remember { mutableStateOf<Uri?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
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
                    .size(50.dp)
                    .padding(top = 15.dp)
                    .clickable { navController.popBackStack() },
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            "AI 자세 분석",
            fontSize = 25.sp,
            color = Color.White
        )
        Text(
            "학생 이름",
            fontSize = 18.sp,
            color = Color.Green
        )
        Spacer(modifier = Modifier.height(40.dp))

        // 이미지 표시
        Box(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .height(240.dp)
                .width(350.dp)
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                .background(Color.Gray),
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
                modifier = Modifier
                    .fillMaxHeight()
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
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.3f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("적정 각도", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.size(10.dp))
                Text("150 도", color = Color.White)
                Spacer(modifier = Modifier.size(5.dp))
                Text("105 도", color = Color.White)
                Spacer(modifier = Modifier.size(5.dp))
                Text("180 도", color = Color.White)
            }
            Spacer(modifier = Modifier.size(15.dp))
            Column(
                modifier = Modifier
                    .fillMaxHeight()
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
                    saveImageToGallery(context, bitmap) // 갤러리에 저장
                    Toast.makeText(context, "이미지가 갤러리에 저장되었습니다!", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.DarkGray,
                contentColor = Color.White
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

// 숫자 포맷 변환
fun Double.format(digits: Int) = "%.${digits}f".format(this)

// 갤러리에 이미지 저장 함수
fun saveImageToGallery(context: Context, bitmap: Bitmap, filename: String = "PoseImage") {
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "$filename.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/PoseReports")
    }

    val contentResolver = context.contentResolver
    val imageUri =
        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    imageUri?.let { uri ->
        val outputStream: OutputStream? = contentResolver.openOutputStream(uri)
        outputStream?.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
    }
}