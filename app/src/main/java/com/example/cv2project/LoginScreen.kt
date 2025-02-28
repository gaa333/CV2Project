package com.example.cv2project

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cv2project.auth.AuthManager
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController, authManager: AuthManager) {
    val coroutineScope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.nextgoal1),
            contentDescription = "login app logo",
            modifier = Modifier.padding(horizontal = 30.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            "로그인", style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("이메일", color = Color.White) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            textStyle = TextStyle(color = Color.White),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Green,
                unfocusedBorderColor = Color.White
            ),
            modifier = Modifier.width(330.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("비밀번호", color = Color.White) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            visualTransformation = PasswordVisualTransformation(), // 입력값이 *로 표시됨
            textStyle = TextStyle(color = Color.White),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Green,
                unfocusedBorderColor = Color.White
            ),
            modifier = Modifier.width(330.dp)
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp),
            border = BorderStroke(2.dp, Color(0xFF4786FF)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
        ) {
            Text("로그인", color = Color.White)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = { navController.navigate("signup") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp),
            border = BorderStroke(2.dp, Color(0xFF4786FF)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
        ) {
            Text("회원가입", color = Color.White)
        }
        Spacer(modifier = Modifier.height(10.dp))

        // 익명 로그인 버튼
//        Button(
//            onClick = {
//                authManager.signInAnonymously { success, error ->
//                    if (success) {
//                        navController.navigate("main") {
//                            popUpTo("login") { inclusive = true }
//                        }
//                    } else {
//                        errorMessage = "익명 로그인 실패: $error"
//                    }
//                }
//            },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 30.dp),
//            border = BorderStroke(2.dp, Color(0xFF4786FF)),
//            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
//        ) {
//            Text("익명으로 로그인", color = Color.White)
//        }
    }
}
