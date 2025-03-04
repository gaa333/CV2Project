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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
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
import com.google.firebase.auth.FirebaseAuth
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

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
    val noticeDb = remember { NoticeDatabase() }
    val announcementDb = remember { AnnouncementDatabase() }
    val studentDb = remember { StudentDatabase() }
    val authManager = remember { AuthManager() }
    var userName by remember { mutableStateOf("알수없음") }
    var userEmail by remember { mutableStateOf("알수없음") }
    var userRole by remember { mutableStateOf("게스트") }
    val auth = FirebaseAuth.getInstance()

    // 인증 상태 변경 리스너 등록
    DisposableEffect(auth) {
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            authManager.getCurrentUserInfo { user ->
                if (user != null) {
                    userName = user.name
                    userEmail = user.email
                    userRole = user.role
                } else {
                    userName = "Unknown"
                    userEmail = "unknown@example.com"
                    userRole = "게스트"
                }
            }
        }
        auth.addAuthStateListener(authStateListener)
        onDispose {
            auth.removeAuthStateListener(authStateListener)
        }
    }

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
        composable("main") { MainScreen(navController, authManager, userRole, userName, userEmail) }
        composable("poseAnalysis") { PoseAnalysisScreen(navController, userRole) }
        composable("notice") { NoticeScreen(navController, noticeDb, userRole) }
        composable("announcement") { AnnouncementScreen(navController, announcementDb, userRole) }
        composable("schedule") { ScheduleScreen(navController, userRole) }
        composable("pickupService") { PickupServiceScreen(navController) }
        composable("payment") { PaymentScreen(navController) }
        composable("studentClassList") { StudentClassListScreen(navController, userRole) }
        composable("performanceReport") {
            PerformanceReportScreen(
                navController,
                studentDb,
                userRole
            )
        }
        composable("addNotice") { AddNoticeScreen(navController, studentDb, noticeDb, userRole) }

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
            val title = backStackEntry.arguments?.getString("title")
                ?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) } ?: "제목 없음"
            val content = backStackEntry.arguments?.getString("content")
                ?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) } ?: "내용 없음"
            val studentName = backStackEntry.arguments?.getString("studentName")
                ?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) } ?: "이름 없음"
            val date = backStackEntry.arguments?.getString("date")
                ?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) } ?: "날짜 없음"

            val notice = Notice(id, title, content, studentName, date)
            DetailNoticeScreen(navController, notice, noticeDb, authManager, userRole)
        }

        // Add Announcement Screen
        composable("addAnnouncement") {
            AddAnnouncementScreen(
                navController,
                announcementDb,
                userRole
            )
        }

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
            // 네비게이션 인자로 전달된 데이터를 추출 후 URL 디코딩 처리
            val id = backStackEntry.arguments?.getString("id") ?: ""
            val title = backStackEntry.arguments?.getString("title")
                ?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) } ?: "제목 없음"
            val content = backStackEntry.arguments?.getString("content")
                ?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) } ?: "내용 없음"
            val date = backStackEntry.arguments?.getString("date")
                ?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) } ?: "날짜 없음"

            val announcement = Announcement(id, title, content, date)
            DetailAnnouncementScreen(navController, announcement, announcementDb, userRole)
        }

        // Student Management Screen
        composable(
            "studentManagement?className={className}",
            arguments = listOf(
                navArgument("className") { type = NavType.StringType; defaultValue = "반 이름 없음" }
            )
        ) { backStackEntry ->
            val className = backStackEntry.arguments?.getString("className") ?: "반 이름 없음"
            StudentManagementScreen(navController, studentDb, className, userRole)
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

            StudentDetailScreen(navController, studentName, studentAge, userRole)
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
                ankleScore,
                userRole
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

            DetailPerformanceReportScreen(navController, id, name, age, userRole)
        }
    }
}

@Composable
fun MainScreen(
    navController: NavHostController,
    authManager: AuthManager,
    userRole: String,
    userName: String,
    userEmail: String
) {
    var showUserDialog by remember { mutableStateOf(false) }

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
                contentDescription = "앱로고"
            )
            Image(
                painter = painterResource(R.drawable.logout),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .offset(x = 18.dp, y = 20.dp)
                    .clickable {
                        authManager.logout()
                        navController.navigate("login") {
                            popUpTo("main") { inclusive = true } // 메인 화면을 스택에서 제거하고 로그인 화면으로 이동
                        }
                    }
            )
            Image(
                painter = painterResource(R.drawable.user),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .offset(x = 300.dp, y = 20.dp)
                    .clickable { showUserDialog = true }
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
    if (showUserDialog) {
        AlertDialog(
            onDismissRequest = { showUserDialog = false },
            title = { Text("사용자 정보", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Name: $userName", fontSize = 17.sp)
                    Spacer(modifier = Modifier.size(10.dp))
                    Text("Email: $userEmail", fontSize = 17.sp)
                    Spacer(modifier = Modifier.size(10.dp))
                    Text("Role: $userRole", fontSize = 17.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showUserDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4786FF))
                ) {
                    Text("확인", color = Color.White)
                }
            }
        )
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