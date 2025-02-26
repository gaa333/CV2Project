package com.example.cv2project

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cv2project.auth.AuthManager
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController, authManager: AuthManager) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("로그인", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("이메일") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("비밀번호") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        errorMessage?.let {
            Text(text = it, color = Color.Red)
            Spacer(modifier = Modifier.height(10.dp))
        }

        Button(
            onClick = {
                coroutineScope.launch {
                    val user = authManager.login(email, password)
                    if (user != null) {
                        navController.navigate("main") // 로그인 성공 시 메인 화면으로 이동
                    } else {
                        errorMessage = "로그인 실패! 이메일 또는 비밀번호를 확인하세요."
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("로그인")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = { navController.navigate("signup") }, // 회원가입 화면 이동
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("회원가입")
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 익명 로그인 버튼 추가 ✅
        Button(
            onClick = {
                authManager.signInAnonymously { success, error ->
                    if (success) {
                        navController.navigate("main") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        errorMessage = "익명 로그인 실패: $error"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("익명으로 로그인")
        }
    }
}
