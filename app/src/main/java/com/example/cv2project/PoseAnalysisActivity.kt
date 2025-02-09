package com.example.cv2project

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.cv2project.ui.theme.CV2ProjectTheme
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.BaseVisionTaskApi
import com.google.mediapipe.tasks.vision.core.ImageProcessingOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tensorflow.lite.support.image.TensorImage
import java.util.concurrent.Executors

class PoseAnalysisActivity : ComponentActivity(), PoseLandmarkerHelper.LandmarkerListener {

    private var poseLandmarkerHelper: PoseLandmarkerHelper? = null
    private var selectedVideoUri: Uri? = null
    private val executorService = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            PoseAnalysisScreen(
                onSelectVideo = { openGallery() },
                videoUri = selectedVideoUri
            )
        }
    }

    /**
     * 갤러리에서 동영상 선택
     */
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "video/*"
        }
        videoPickerLauncher.launch(intent)
    }

    /**
     * 갤러리에서 선택한 비디오를 처리
     */
    private val videoPickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    selectedVideoUri = uri
                    processVideo(uri)
                }
            }
        }

    /**
     * 동영상에서 포즈 감지 실행
     */
    private fun processVideo(videoUri: Uri) {
        poseLandmarkerHelper = PoseLandmarkerHelper(
            context = this,
            runningMode = RunningMode.VIDEO,
            currentModel = PoseLandmarkerHelper.MODEL_POSE_LANDMARKER_LITE,
            poseLandmarkerHelperListener = this
        )

        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(this, videoUri)
        val videoLengthMs =
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong()
                ?: return

        val inferenceIntervalMs = 200L // 200ms 간격으로 프레임을 분석
        val numFrames = videoLengthMs / inferenceIntervalMs

        val results = mutableListOf<PoseLandmarkerResult>()

        for (i in 0 until numFrames) {
            val timestampMs = i * inferenceIntervalMs
            retriever.getFrameAtTime(
                timestampMs * 1000,
                MediaMetadataRetriever.OPTION_CLOSEST
            )?.let { frame ->
                val bitmap = frame.copy(Bitmap.Config.ARGB_8888, false)
                val mpImage = BitmapImageBuilder(bitmap).build()

                val result = poseLandmarkerHelper?.detectVideoFile(this, videoUri)  // ✅ `this` 추가 (Context 전달)


                result?.let { results.addAll(it.results) }
            }
        }

        retriever.release()
        Log.d("PoseAnalysis", "Processed ${results.size} frames for pose estimation")
    }

    override fun onError(error: String, errorCode: Int) {
        Log.e("PoseAnalysis", "Error: $error")
        runOnUiThread {
            Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResults(resultBundle: PoseLandmarkerHelper.ResultBundle) {
        Log.d("PoseAnalysis", "Pose Detected: ${resultBundle.results.size}")
    }
}

/**
 * Jetpack Compose UI
 */
@Composable
fun PoseAnalysisScreen(onSelectVideo: () -> Unit, videoUri: Uri?) {
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        Button(onClick = onSelectVideo) {
            Text("Select Video from Gallery")
        }

        videoUri?.let {
            Text(text = "Selected video: $videoUri")
        }
    }
}


//// 자세 분석
//@Composable
//fun PoseAnalysisScreen() {
//    val context = LocalContext.current as? Activity
//    val coroutineScope = rememberCoroutineScope()
//    var videoUri by remember { mutableStateOf<Uri?>(null) }
//    var analyzedImage by remember { mutableStateOf<Bitmap?>(null) }
//    var analyzedFrames by remember { mutableStateOf<List<Bitmap>>(emptyList()) }
//    // ✅ 갤러리에서 영상 선택하는 런처
//    val pickVideoLauncher =
//        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
//            videoUri = uri
//        }
//
//
//
//
//    Column(
//        modifier = Modifier.fillMaxSize(),
////        verticalArrangement = Arrangement.Top,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text("자세 분석", fontSize = 40.sp)
//
//        // "영상 선택" 버튼
//        Button(
//            onClick = {
//                pickVideoLauncher.launch("video/*")
//            }
//        ) {
//            Text("갤러리에서 영상 선택")
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Column(
//            modifier = Modifier
//                .height(200.dp)
//                .fillMaxWidth()
//        ) {
//            // 선택한 영상 표시
//            videoUri?.let {
//                VideoPlayer(uri = it)
//            }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
////        // "영상 분석" 버튼
////        Button(onClick = {
////            videoUri?.let { uri ->
////                coroutineScope.launch {
////                    analyzedFrames = context?.let { processVideo(it, uri) }!!
////                }
////            }
////        }) {
////            Text("영상 분석")
////        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
////        Column(
////            modifier = Modifier
////                .fillMaxSize()
////                .background(color = androidx.compose.ui.graphics.Color.LightGray)
////        ) {
////            // ✅ 분석된 결과 표시
////            // 분석된 프레임 표시
////            LazyRow {
////                itemsIndexed(analyzedFrames) { index, bitmap ->
////                    Image(
////                        bitmap = bitmap.asImageBitmap(),
////                        contentDescription = "Analyzed Frame ${index + 1}",
////                        modifier = Modifier
////                            .width(200.dp)
////                            .height(300.dp)
////                    )
////                }
////            }
////        }
//
//        Button(
//            onClick = {
//                context?.finish()
//            }
//        ) {
//            Text("뒤로")
//        }
//    }
//}
//
//@Composable
//fun VideoPlayer(uri: Uri) {
//    val context = LocalContext.current
//    val exoPlayer = remember {
//        ExoPlayer.Builder(context).build().apply {
//            setMediaItem(MediaItem.fromUri(uri))
//            prepare()
//        }
//    }
//
//    AndroidView(
//        factory = { ctx ->
//            PlayerView(ctx).apply {
//                player = exoPlayer
//            }
//        },
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(200.dp) // ✅ 화면 중앙에 배치
//    )
//}
//
////suspend fun processVideo(context: Context, uri: Uri): List<Bitmap> = withContext(Dispatchers.Default) {
////    val resultBitmaps = mutableListOf<Bitmap>()
////
////    try {
////        val baseOptions = BaseOptions.builder()
////            .setModelAssetPath("pose_landmarker/pose_landmarker_lite.task")
////            .build()
////        val options = PoseLandmarker.PoseLandmarkerOptions.builder()
////            .setBaseOptions(baseOptions)
////            .setRunningMode(RunningMode.VIDEO)
////            .build()
////
////        PoseLandmarker.createFromOptions(context, options)?.use { poseLandmarker ->
////            val retriever = MediaMetadataRetriever()
////            retriever.setDataSource(context, uri)
////
////            val frameRate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CAPTURE_FRAMERATE)?.toFloatOrNull() ?: 30f
////            val durationMs = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
////
////            for (timeMs in 0L..durationMs step (1000 / frameRate).toLong()) {
////                val bitmap = retriever.getFrameAtTime(timeMs * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
////                bitmap?.let {
////                    val mpImage = BitmapImageBuilder(it).build()
////                    val result = poseLandmarker.detectForVideo(mpImage, timeMs)
////                    val processedBitmap = drawPoseOnBitmap(it, result)
////                    resultBitmaps.add(processedBitmap)
////                    Log.d("ProcessVideo", "Processed frame at $timeMs ms")
////                }
////            }
////
////            retriever.release()
////        }
////    } catch (e: Exception) {
////        Log.e("ProcessVideo", "Error processing video", e)
////    }
////
////    resultBitmaps
////}
////
////fun drawPoseOnBitmap(bitmap: Bitmap, result: PoseLandmarkerResult): Bitmap {
////    val outputBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
////    val canvas = Canvas(outputBitmap)
////    val paint = Paint().apply {
////        color = Color.RED
////        strokeWidth = 3f
////        style = Paint.Style.STROKE
////    }
////
////    result.landmarks().forEach { poseLandmarks ->
////        poseLandmarks.forEach { landmark ->
////            val x = landmark.x() * bitmap.width
////            val y = landmark.y() * bitmap.height
////            canvas.drawCircle(x, y, 5f, paint)
////        }
////    }
////
////    return outputBitmap
////}
//
//
///*
//fun processVideo(context: android.content.Context, videoUri: Uri): Bitmap? {
//    val retriever = MediaMetadataRetriever()
//    retriever.setDataSource(context, videoUri)
//
//    // ✅ 첫 번째 프레임을 가져와서 분석
//    val frameBitmap: Bitmap? =
//        retriever.getFrameAtTime(1000000, MediaMetadataRetriever.OPTION_CLOSEST) // 1초 후 프레임
//    retriever.release()
//
//    frameBitmap?.let { bitmap ->
//        return applyPoseEstimation(context, bitmap)
//    }
//    return null
//}
//
//fun applyPoseEstimation(context: Context, bitmap: Bitmap): Bitmap {
//    // MediaPipe Pose Landmarker 초기화
//    val baseOptions = BaseOptions.builder()
//        .setModelAssetPath("assets/pose_landmarker_lite.task")
//        .build()
//
//    val minPoseDetectionConfidence = 0.1f
//    val minPosePresenceConfidence = 0.1f
//    val minTrackingConfidence = 0.1f
//    val maxNumPoses = 1
//
//
//    val options = PoseLandmarker.PoseLandmarkerOptions.builder()
//        .setBaseOptions(baseOptions)
//        .setMinPoseDetectionConfidence(minPoseDetectionConfidence)
//        .setMinPosePresenceConfidence(minPosePresenceConfidence)
//        .setMinTrackingConfidence(minTrackingConfidence)
//        .setNumPoses(maxNumPoses)
//        .setOutputSegmentationMasks(false)
//        .setRunningMode(RunningMode.IMAGE)
//        .build()
//
//
//    val poseLandmarker = PoseLandmarker.createFromOptions(context, options)
//
//    // Bitmap을 MPImage로 변환
//    val mpImage = BitmapImageBuilder(bitmap).build()
//
//    // MPImage 입력으로 포즈 감지 수행
//    val result = poseLandmarker.detect(mpImage)
//
//    // 감지된 결과를 비트맵에 오버레이
//    return drawPoseOnBitmap(bitmap, result)
//}
//
//
//// ✅ 포즈 데이터를 비트맵에 오버레이
//fun drawPoseOnBitmap(bitmap: Bitmap, result: PoseLandmarkerResult): Bitmap {
//    val newBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true) ?: Bitmap.createBitmap(
//        bitmap.width,
//        bitmap.height,
//        Bitmap.Config.ARGB_8888
//    )
//    val canvas = Canvas(newBitmap)
//    val paint = Paint().apply {
//        color = Color.RED
//        style = Paint.Style.FILL
//        strokeWidth = 5f
//    }
//
//    // 최신 API에 맞게 수정
//    result.landmarks().forEach { poseLandmarks ->
//        poseLandmarks.forEach { landmark ->
//            val x = (landmark.x() * newBitmap.width).toInt()
//            val y = (landmark.y() * newBitmap.height).toInt()
//            canvas.drawCircle(x.toFloat(), y.toFloat(), 8f, paint)
//        }
//    }
//
//    return newBitmap
//}
//*/