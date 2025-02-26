@file:OptIn(ExperimentalAnimationApi::class)

package com.example.cv2project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.unit.dp
import com.example.cv2project.ui.theme.CV2ProjectTheme
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.statusBars
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.cv2project.auth.AuthManager
import com.example.cv2project.firebase.AnnouncementDatabase
import com.example.cv2project.firebase.NoticeDatabase
import com.example.cv2project.firebase.StudentDatabase
import com.example.cv2project.models.Announcement
import com.example.cv2project.models.Notice
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
    val noticeDb = remember { NoticeDatabase() }
    val announcementDb = remember { AnnouncementDatabase() }
    val studentDb = remember { StudentDatabase() }
    val authManager = remember { AuthManager() }

    AnimatedNavHost(
        navController = navController,
        startDestination = "splash",
        enterTransition = { fadeIn(animationSpec = tween(0)) },
        exitTransition = { fadeOut(animationSpec = tween(0)) },
        popEnterTransition = { fadeIn(animationSpec = tween(0)) },
        popExitTransition = { fadeOut(animationSpec = tween(0)) }
    ) {
        composable("splash") { SplashScreen(navController, authManager) }
        composable("login") { LoginScreen(navController, authManager) }
        composable("signup") { SignUpScreen(navController, authManager) }
        composable("main") { MainScreen(navController, authManager) }
        composable("poseAnalysis") { PoseAnalysisScreen(navController) }
        composable("notice") { NoticeScreen(navController, noticeDb) }
        composable("announcement") { AnnouncementScreen(navController, announcementDb) }
        composable("schedule") { ScheduleScreen(navController) }
        composable("pickupService") { PickupServiceScreen(navController) }
        composable("payment") { PaymentScreen(navController) }
        composable("studentClassList") { StudentClassListScreen(navController) }
        composable("performanceReport") { PerformanceReportScreen(navController, studentDb) }
        composable("addNotice") { AddNoticeScreen(navController, studentDb, noticeDb) }

        // Detail Notice Screen
        composable(
            route = "detailNotice?id={id}&title={title}&content={content}&studentName={studentName}&date={date}",
            arguments = listOf(
                navArgument("id") { type = NavType.StringType },
                navArgument("title") { type = NavType.StringType },
                navArgument("content") { type = NavType.StringType },
                navArgument("studentName") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            val title = backStackEntry.arguments?.getString("title") ?: "제목 없음"
            val content = backStackEntry.arguments?.getString("content") ?: "내용 없음"
            val studentName = backStackEntry.arguments?.getString("studentName") ?: "이름 없음"
            val date = backStackEntry.arguments?.getString("date") ?: "날짜 없음"

            val notice = Notice(id, title, content, studentName, date)
            DetailNoticeScreen(navController, notice, noticeDb)
        }

        // Add Announcement Screen
        composable("addAnnouncement") { AddAnnouncementScreen(navController, announcementDb) }

        // Detail Announcement Screen
        composable(
            route = "detailAnnouncement?id={id}&title={title}&content={content}&date={date}",
            arguments = listOf(
                navArgument("id") { type = NavType.StringType },
                navArgument("title") { type = NavType.StringType },
                navArgument("content") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            // 네비게이션 인자로 전달된 데이터를 추출
            val id = backStackEntry.arguments?.getString("id") ?: ""
            val title = backStackEntry.arguments?.getString("title") ?: "제목 없음"
            val content = backStackEntry.arguments?.getString("content") ?: "내용 없음"
            val date = backStackEntry.arguments?.getString("date") ?: "날짜 없음"
            val announcement = Announcement(id, title, content, date)
            DetailAnnouncementScreen(navController, announcement, announcementDb)
        }

        // Student Management Screen
        composable(
            "studentManagement?className={className}",
            arguments = listOf(
                navArgument("className") { type = NavType.StringType; defaultValue = "반 이름 없음" }
            )
        ) { backStackEntry ->
            val className = backStackEntry.arguments?.getString("className") ?: "반 이름 없음"
            StudentManagementScreen(navController, studentDb, className)
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
            "detailPerformanceReport?id={id}&name={name}&age={age}",
            arguments = listOf(
                navArgument("id") { type = NavType.StringType; defaultValue = "" }, // ✅ id 추가
                navArgument("name") { type = NavType.StringType; defaultValue = "Unknown" },
                navArgument("age") { type = NavType.IntType; defaultValue = 0 }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            val name = backStackEntry.arguments?.getString("name") ?: "Unknown"
            val age = backStackEntry.arguments?.getInt("age") ?: 0

            DetailPerformanceReportScreen(navController, id, name, age)
        }
    }
}

@Composable
fun MainScreen(navController: NavHostController, authManager: AuthManager) {
    Column(
        modifier = Modifier
            .padding(WindowInsets.statusBars.only(WindowInsetsSides.Top).asPaddingValues())
            .background(Color.Black),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box() {
            Image(
                painter = painterResource(id = R.drawable.nextgoal),
                contentDescription = "앱로고",
            )
            Image(
                painter = painterResource(R.drawable.logout),
                contentDescription = "로그아웃",
                modifier = Modifier
                    .size(40.dp)
                    .offset(x = 20.dp, y = 24.dp)
                    .clickable {
                        authManager.logout()
                        navController.navigate("login") {
                            popUpTo("main") { inclusive = true }
                        }
                    }
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
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