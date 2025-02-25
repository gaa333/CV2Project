@file:OptIn(ExperimentalAnimationApi::class)

package com.example.cv2project

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.ComponentActivity.RESULT_OK
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cv2project.ui.theme.CV2ProjectTheme
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.statusBars
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.max
import androidx.compose.foundation.Canvas
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.cv2project.preferences.AnnouncementPreferences
import com.example.cv2project.preferences.CommentPreferences
import com.example.cv2project.preferences.NoticePreferences
import com.example.cv2project.preferences.StudentPreferences
import com.google.accompanist.navigation.animation.AnimatedNavHost


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CV2ProjectTheme {
                MyApp()
            }
        }
    }
}

@Composable
fun MyApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val studentPrefs = remember { StudentPreferences(context) }
    val noticePrefs = remember { NoticePreferences(context) }
    val commentPrefs = remember { CommentPreferences(context) }
    val announcementPrefs = remember { AnnouncementPreferences(context) }


    AnimatedNavHost(
        navController = navController,
        startDestination = "main",
        enterTransition = { fadeIn(animationSpec = tween(0)) },
        exitTransition = { fadeOut( animationSpec = tween(0)) },
        popEnterTransition = { fadeIn(animationSpec = tween(0)) },
        popExitTransition = { fadeOut(animationSpec = tween(0)) }
    ) {
        composable("main") { MainScreen(navController) }
        composable("poseAnalysis") { PoseAnalysisScreen(navController) }
        composable("notice") { NoticeScreen(navController) }
        composable("announcement") { AnnouncementScreen(navController) }
        composable("schedule") { ScheduleScreen(navController) }
        composable("pickupService") { PickupServiceScreen(navController) }
        composable("payment") { PaymentScreen(navController) }
        composable("studentClassList") { StudentClassListScreen(navController) }
        composable("performanceReport") { PerformanceReportScreen(navController, studentPrefs) }
        composable("addNotice") { AddNoticeScreen(navController, studentPrefs, noticePrefs) }

        // Detail Notice Screen
        composable(
            route = "detailNotice?title={title}&content={content}&studentName={studentName}&date={date}&noticeId={noticeId}",
            arguments = listOf(
                navArgument("title") { type = NavType.StringType; defaultValue = "제목 없음" },
                navArgument("content") { type = NavType.StringType; defaultValue = "내용 없음" },
                navArgument("studentName") { type = NavType.StringType; defaultValue = "이름 없음" },
                navArgument("date") { type = NavType.StringType; defaultValue = "날짜 없음" },
                navArgument("noticeId") { type = NavType.StringType; defaultValue = "noticeId" }
            )
        ) { backStackEntry ->
            // 인자(Arguments) 추출
            val title = backStackEntry.arguments?.getString("title") ?: "제목 없음"
            val content = backStackEntry.arguments?.getString("content") ?: "내용 없음"
            val studentName = backStackEntry.arguments?.getString("studentName") ?: "이름 없음"
            val date = backStackEntry.arguments?.getString("date") ?: "날짜 없음"
            val noticeId =
                backStackEntry.arguments?.getString("noticeId") ?: "noticeId" // ✅ noticeId 추출

            DetailNoticeScreen(
                navController,
                title = title,
                content = content,
                studentName = studentName,
                date = date,
                noticeId = noticeId,
                commentPrefs = commentPrefs,
                noticePrefs
            )
        }

        // Add Announcement Screen
        composable("addAnnouncement") { AddAnnouncementScreen(navController, announcementPrefs) }

        // Detail Announcement Screen
        composable(
            route = "detailAnnouncement?title={title}&content={content}&date={date}",
            arguments = listOf(
                navArgument("title") { type = NavType.StringType },
                navArgument("content") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            // 네비게이션 인자로 전달된 데이터를 추출
            val title = backStackEntry.arguments?.getString("title") ?: "제목 없음"
            val content = backStackEntry.arguments?.getString("content") ?: "내용 없음"
            val date = backStackEntry.arguments?.getString("date") ?: "날짜 없음"

            DetailAnnouncementScreen(navController, title, content, date, announcementPrefs)
        }

        // Student Management Screen
        composable(
            "studentManagement?className={className}",
            arguments = listOf(
                navArgument("className") { type = NavType.StringType; defaultValue = "반 이름 없음" }
            )
        ) { backStackEntry ->
            val className = backStackEntry.arguments?.getString("className") ?: "반 이름 없음"
            StudentManagementScreen(navController, studentPrefs, className)
        }

        // Student Detail Screen
        composable(
            "studentDetail?studentName={studentName}&studentAge={studentAge}",
            arguments = listOf(
                navArgument("studentName") { type = NavType.StringType; defaultValue = "이름 없음" },
                navArgument("studentAge") { type = NavType.IntType; defaultValue = 0 }
            )
        ) { backStackEntry ->
            val studentName = backStackEntry.arguments?.getString("studentName") ?: "이름 없음"
            val studentAge = backStackEntry.arguments?.getInt("studentAge") ?: 0

            StudentDetailScreen(navController, studentName, studentAge)
        }

        // Pose Report Screen
        composable(
            "poseReport?imagePath={imagePath}&hipAngle={hipAngle}&kneeAngle={kneeAngle}&ankleAngle={ankleAngle}&hipScore={hipScore}&kneeScore={kneeScore}&ankleScore={ankleScore}",
            arguments = listOf(
                navArgument("imagePath") { type = NavType.StringType; defaultValue = "" },
                navArgument("hipAngle") { type = NavType.FloatType; defaultValue = 0.0f },
                navArgument("kneeAngle") { type = NavType.FloatType; defaultValue = 0.0f },
                navArgument("ankleAngle") { type = NavType.FloatType; defaultValue = 0.0f },
                navArgument("hipScore") { type = NavType.FloatType; defaultValue = 0.0f },
                navArgument("kneeScore") { type = NavType.FloatType; defaultValue = 0.0f },
                navArgument("ankleScore") { type = NavType.FloatType; defaultValue = 0.0f }
            )
        ) { backStackEntry ->
            val imagePath = backStackEntry.arguments?.getString("imagePath")
            val hipAngle = backStackEntry.arguments?.getFloat("hipAngle")?.toDouble() ?: 0.0
            val kneeAngle = backStackEntry.arguments?.getFloat("kneeAngle")?.toDouble() ?: 0.0
            val ankleAngle = backStackEntry.arguments?.getFloat("ankleAngle")?.toDouble() ?: 0.0
            val hipScore = backStackEntry.arguments?.getFloat("hipScore")?.toDouble() ?: 0.0
            val kneeScore = backStackEntry.arguments?.getFloat("kneeScore")?.toDouble() ?: 0.0
            val ankleScore = backStackEntry.arguments?.getFloat("ankleScore")?.toDouble() ?: 0.0

            PoseReportScreen(
                navController,
                imagePath,
                hipAngle,
                kneeAngle,
                ankleAngle,
                hipScore,
                kneeScore,
                ankleScore
            )
        }

        // Detail Performance Report Screen
        composable(
            "detailPerformanceReport?name={name}&age={age}",
            arguments = listOf(
                navArgument("name") { type = NavType.StringType; defaultValue = "Unknown" },
                navArgument("age") { type = NavType.IntType; defaultValue = 0 }
            )
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: "Unknown"
            val age = backStackEntry.arguments?.getInt("age") ?: 0

            DetailPerformanceReportScreen(navController, name, age)
        }
    }
}

@Composable
fun MainScreen(navController: NavHostController) {
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
        Spacer(modifier = Modifier.height(30.dp))
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
                    .height(130.dp)
                    .align(Alignment.Start)
                    .padding(start = 10.dp, top = 10.dp)
            )
            Spacer(modifier = Modifier.weight(0.06f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MenuButton(R.drawable.notice, navController, "notice") //알림장
                MenuButton(
                    R.drawable.announcement,
                    navController,
                    "announcement"
                ) //공지사항
                MenuButton(R.drawable.schedule, navController, "schedule") //일정표
            }
            Spacer(modifier = Modifier.weight(0.05f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MenuButton(R.drawable.pickup, navController, "pickupService") //픽업서비스
                MenuButton(R.drawable.pay, navController, "payment") //원비결제
                MenuButton(R.drawable.student, navController, "studentClassList") //학생관리
            }
            Spacer(modifier = Modifier.weight(0.05f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MenuButton(R.drawable.pose, navController, "poseAnalysis") //자세분석
                MenuButton(
                    R.drawable.report,
                    navController,
                    "performanceReport"
                ) //성과보고서
            }
            Spacer(modifier = Modifier.weight(0.1f))
        }
    }
}

@Composable
fun MenuButton(imageResId: Int, navController: NavController, route: String) {
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
                        navController.navigate(route)
                    }
                )
            }
            .scale(scale)
            .size(90.dp) // 이미지 크기 조정
    )
}

