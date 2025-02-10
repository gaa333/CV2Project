@file:OptIn(ExperimentalAnimationApi::class)

package com.example.cv2project

import VideoEncoder
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
import java.io.File
import java.util.concurrent.Executors
import android.media.MediaCodec
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import java.io.FileOutputStream
import kotlin.math.max
import androidx.compose.foundation.Canvas

class PoseAnalysisActivity : ComponentActivity(), PoseLandmarkerHelper.LandmarkerListener {

    private val viewModel: PoseAnalysisViewModel by viewModels()
    private var poseLandmarkerHelper: PoseLandmarkerHelper? = null
    private val executorService = Executors.newSingleThreadExecutor()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
//                val viewModel: PoseAnalysisViewModel = viewModel()

            CV2ProjectTheme {
                val frames by viewModel.frames.collectAsState(initial = emptyList())
                PoseAnalysisScreen(
                    onSelectVideo = { openGallery(viewModel) },
                    frames = frames
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
fun PoseAnalysisScreen(onSelectVideo: () -> Unit, frames: List<Bitmap>) {
    val context = LocalContext.current as? Activity
    var currentFrameIndex by remember { mutableStateOf(0) }

    LaunchedEffect(frames) {
        while (frames.isNotEmpty()) {
            delay(500L) // Control the frame rate
            currentFrameIndex = (currentFrameIndex + 1) % frames.size
            Log.d("FrameUpdate", "✅ Current frame index updated: $currentFrameIndex") // 로그 추가
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
        ) {
            if (frames.isNotEmpty()) {
                PoseAnimation(frames)
            } else {
                Text("no frames")
            }
        }

        Button(onClick = onSelectVideo) {
            Text("Select Video from Gallery")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { }) {
            Text("Back")
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