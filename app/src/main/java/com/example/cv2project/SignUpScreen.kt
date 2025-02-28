package com.example.cv2project

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cv2project.auth.AuthManager
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(navController: NavController, authManager: AuthManager) {
    val coroutineScope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val roles = listOf("admin", "학부모", "학생", "게스트")
    var selectedRole by remember { mutableStateOf("게스트") }
    var expanded by remember { mutableStateOf(false) }
    var showInviteDialog by remember { mutableStateOf(false) }
    var inviteCode by remember { mutableStateOf("") }
    val adminInviteCode = "garam" // 관리자 초대 코드
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.nextgoal1),
            contentDescription = "로그인 앱 로고",
            modifier = Modifier.padding(horizontal = 30.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            "회원가입", style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("이름", color = Color.White) },
            textStyle = TextStyle(color = Color.White),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Green,
                unfocusedBorderColor = Color.White
            ),
            modifier = Modifier.width(330.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("이메일", color = Color.White) },
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
            label = { Text("비밀번호(6자리 이상 입력)", color = Color.White) },
            textStyle = TextStyle(color = Color.White),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Green,
                unfocusedBorderColor = Color.White
            ),
            modifier = Modifier.width(330.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("비밀번호 확인", color = Color.White) },
            textStyle = TextStyle(color = Color.White),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Green,
                unfocusedBorderColor = Color.White
            ),
            modifier = Modifier.width(330.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            "회원 유형 선택", fontSize = 18.sp, color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))

        Box {
            OutlinedButton(
                onClick = { expanded = true },
                border = BorderStroke(1.dp, Color.White),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                modifier = Modifier.width(120.dp)
            ) {
                Text(selectedRole, color = Color.White)
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                roles.forEach { role ->
                    DropdownMenuItem(
                        text = { Text(role) },
                        onClick = {
                            selectedRole = role
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        errorMessage?.let {
            Text(text = it, color = Color.Red)
            Spacer(modifier = Modifier.height(10.dp))
        }

        Button(
            onClick = lambda@{
                if (name.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                    errorMessage = "모든 필드를 입력해주세요."
                    return@lambda
                }
                if (password != confirmPassword) {
                    errorMessage = "비밀번호가 일치하지 않습니다."
                    return@lambda
                }
                if (selectedRole == "admin") {
                    // 관리자 계정인 경우 초대 코드 입력 다이얼로그 표시
                    showInviteDialog = true
                } else {
                    coroutineScope.launch {
                        val user = authManager.signUp(name, email, password, selectedRole)
                        if (user != null) {
                            Toast.makeText(context, "회원가입 성공!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        } else {
                            errorMessage = "회원가입 실패! 이메일 형식을 확인하세요."
                        }
                    }
                }
            },
            modifier = Modifier.width(330.dp),
            border = BorderStroke(2.dp, Color(0xFF4786FF)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
        ) {
            Text("회원가입", color = Color.White)
        }
    }
    var inviteErrorMessage by remember { mutableStateOf("") }

    if (showInviteDialog) {
        AlertDialog(
            onDismissRequest = {
                showInviteDialog = false
                inviteErrorMessage = "" // 다이얼로그 닫을 때 에러 메시지 초기화
            },
            title = { Text("초대 코드 입력", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("관리자 계정을 생성하려면 초대 코드가 필요합니다.")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = inviteCode,
                        onValueChange = {
                            inviteCode = it
                            inviteErrorMessage = "" // 사용자가 입력하면 에러 메시지 초기화
                        },
                        label = { Text("초대 코드") }
                    )
                    if (inviteErrorMessage.isNotEmpty()) {
                        Text(text = inviteErrorMessage, color = Color.Red)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (inviteCode == adminInviteCode) {
                            showInviteDialog = false
                            // 초대 코드가 맞으면 회원가입 진행
                            coroutineScope.launch {
                                val user = authManager.signUp(name, email, password, selectedRole)
                                if (user != null) {
                                    Toast.makeText(context, "회원가입 성공!", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                } else {
                                    errorMessage = "회원가입 실패! 이메일 형식을 확인하세요."
                                }
                            }
                        } else {
                            inviteErrorMessage = "올바른 초대 코드가 필요합니다."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4786FF))
                ) {
                    Text("확인", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showInviteDialog = false
                        inviteErrorMessage = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4786FF))
                ) {
                    Text("취소", color = Color.White)
                }
            }
        )
    }
}
