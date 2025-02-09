package com.example.cv2project

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tensorflow.lite.support.image.TensorImage

class PoseAnalysisActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CV2ProjectTheme {
                PoseAnalysisScreen()
            }
        }
    }
}

// 자세 분석
@Composable
fun PoseAnalysisScreen() {
    val context = LocalContext.current as? Activity
    val coroutineScope = rememberCoroutineScope()
    var videoUri by remember { mutableStateOf<Uri?>(null) }
    var analyzedImage by remember { mutableStateOf<Bitmap?>(null) }
    var analyzedFrames by remember { mutableStateOf<List<Bitmap>>(emptyList()) }

    // 갤러리에서 영상 선택
    val pickVideoLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            videoUri = uri
        }

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
                .background(androidx.compose.ui.graphics.Color.Gray)
        ) {
            videoUri?.let {
                VideoPlayer(uri = it)
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
                    pickVideoLauncher.launch("video/*")
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
                    pickVideoLauncher.launch("video/*")
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
                context?.let {
                    val intent = Intent(it, PoseReportActivity::class.java)
                    it.startActivity(intent)
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
                text = "AI 자세 분석",
                fontSize = 18.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}


@Composable
fun VideoPlayer(uri: Uri) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
        }
    }

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                layoutParams = android.widget.FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
    )
}

suspend fun processVideo(context: Context, uri: Uri): List<Bitmap> =
    withContext(Dispatchers.Default) {
        val resultBitmaps = mutableListOf<Bitmap>()

        try {
            val baseOptions = BaseOptions.builder()
                .setModelAssetPath("pose_landmarker/pose_landmarker_lite.task")
                .build()
            val options = PoseLandmarker.PoseLandmarkerOptions.builder()
                .setBaseOptions(baseOptions)
                .setRunningMode(RunningMode.VIDEO)
                .build()

            PoseLandmarker.createFromOptions(context, options)?.use { poseLandmarker ->
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(context, uri)

                val frameRate =
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CAPTURE_FRAMERATE)
                        ?.toFloatOrNull() ?: 30f
                val durationMs =
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                        ?.toLongOrNull() ?: 0L

                for (timeMs in 0L..durationMs step (1000 / frameRate).toLong()) {
                    val bitmap = retriever.getFrameAtTime(
                        timeMs * 1000,
                        MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                    )
                    bitmap?.let {
                        val mpImage = BitmapImageBuilder(it).build()
                        val result = poseLandmarker.detectForVideo(mpImage, timeMs)
                        val processedBitmap = drawPoseOnBitmap(it, result)
                        resultBitmaps.add(processedBitmap)
                        Log.d("ProcessVideo", "Processed frame at $timeMs ms")
                    }
                }

                retriever.release()
            }
        } catch (e: Exception) {
            Log.e("ProcessVideo", "Error processing video", e)
        }

        resultBitmaps
    }

fun drawPoseOnBitmap(bitmap: Bitmap, result: PoseLandmarkerResult): Bitmap {
    val outputBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
    val canvas = Canvas(outputBitmap)
    val paint = Paint().apply {
        color = Color.RED
        strokeWidth = 3f
        style = Paint.Style.STROKE
    }

    result.landmarks().forEach { poseLandmarks ->
        poseLandmarks.forEach { landmark ->
            val x = landmark.x() * bitmap.width
            val y = landmark.y() * bitmap.height
            canvas.drawCircle(x, y, 5f, paint)
        }
    }

    return outputBitmap
}