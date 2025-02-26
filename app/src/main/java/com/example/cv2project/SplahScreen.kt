package com.example.cv2project

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cv2project.auth.AuthManager

@Composable
fun SplashScreen(navController: NavController, authManager: AuthManager) {
    val user = authManager.getCurrentUser()

    LaunchedEffect(Unit) {
        when {
            user == null -> {
                // 로그인하지 않은 경우 로그인 화면으로 이동
                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                }
            }
            user.isAnonymous -> {
                // 익명 로그인한 경우 자동 로그아웃 후 로그인 화면으로 이동
                authManager.logout()
                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                }
            }
            else -> {
                // 정상 로그인한 경우 메인 화면으로 이동
                navController.navigate("main") {
                    popUpTo("splash") { inclusive = true }
                }
            }
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("앱 로딩 중...", fontSize = 20.sp)
    }
}
