package com.example.cv2project

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

/**
 * 원비 결제 Composable
 * - Navigation Graph에서 "payment"로 등록하고 navController.navigate("payment")로 이동
 */
@Composable
fun PaymentScreen(navController: NavController) {
    val context = LocalContext.current
    var checked by remember { mutableStateOf(false) }

    var showTextField1 by remember { mutableStateOf(false) }
    var showTextField2 by remember { mutableStateOf(false) }
    var showTextField3 by remember { mutableStateOf(false) }
    var showTextField4 by remember { mutableStateOf(false) }

    var text1 by remember { mutableStateOf("") }
    var text2 by remember { mutableStateOf("") }
    var text3 by remember { mutableStateOf("") }
    var text4 by remember { mutableStateOf("") }

    val isAllFieldsFilled =
        text1.isNotEmpty() && text2.isNotEmpty() && text3.isNotEmpty() && text4.isNotEmpty()
    val isButtonEnabled = isAllFieldsFilled && checked

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.systemBars.asPaddingValues()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 상단 바
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(color = Color.Black),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "뒤로가기",
                modifier = Modifier
                    .padding(start = 15.dp)
                    .size(25.dp)
                    .clickable {
                        // Activity finish() 대신 Navigation 뒤로가기
                        navController.popBackStack()
                    },
                tint = Color.White
            )
            Text("원비 결제", color = Color.White, fontSize = 25.sp)
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "공유",
                modifier = Modifier
                    .padding(end = 15.dp)
                    .size(25.dp),
                tint = Color.White
            )
        }

        // 내용
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(15.dp)
                .padding(bottom = 20.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.size(15.dp))
            Text("대상 정보 입력하기 *", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.size(10.dp))

            // 청구대상
            Text("청구대상", fontSize = 15.sp)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .border(width = 1.dp, color = Color.Gray, RoundedCornerShape(10.dp))
                    .clickable { showTextField1 = true },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text1.ifEmpty { "청구대상 입력 (최대 20자)" },
                    color = Color.Gray,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
            Text("반드시 본인의 실명을 입력하세요.", color = Color.Gray, fontSize = 10.sp)

            Spacer(modifier = Modifier.size(10.dp))
            Text("휴대전화번호", fontSize = 15.sp)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .border(width = 1.dp, color = Color.Gray, RoundedCornerShape(10.dp))
                    .clickable { showTextField2 = true },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text2.ifEmpty { "휴대전화번호 입력" },
                    color = Color.Gray,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
            Text("반드시 본인 휴대전화번호를 입력하세요.", color = Color.Gray, fontSize = 10.sp)

            // 청구 정보 입력
            Spacer(modifier = Modifier.size(20.dp))
            Text("청구 정보 입력하기 *", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.size(10.dp))

            // 청구항목
            Text("청구항목", fontSize = 15.sp)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .border(width = 1.dp, color = Color.Gray, RoundedCornerShape(10.dp))
                    .clickable { showTextField3 = true },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text3.ifEmpty { "청구항목 입력 (최대 20자)" },
                    color = Color.Gray,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }

            // 청구금액
            Spacer(modifier = Modifier.size(10.dp))
            Text("청구금액 (원)", fontSize = 15.sp)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .border(width = 1.dp, color = Color.Gray, RoundedCornerShape(10.dp))
                    .clickable { showTextField4 = true },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text4.ifEmpty { "청구금액 입력" },
                    color = Color.Gray,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 개인정보 수집동의 영역
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color = Color(0xffE2E2E2)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Column(
                    modifier = Modifier
                        .weight(0.5f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { checked = it },
                            modifier = Modifier
                                .size(30.dp)
                                .padding(start = 10.dp)
                        )
                        Text(
                            "개인정보 수집이용 및 마케팅 정보 수신동의 (필수)",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    }
                }
                Divider(
                    thickness = 1.dp,
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp)
                )
                Column(
                    modifier = Modifier
                        .weight(0.5f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "개인정보 수집동의 안내",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 10.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(end = 5.dp)
                                .size(30.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.size(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color = Color(0xffE2E2E2)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("청구서 발송 시, 문자로 전송됩니다.", fontSize = 12.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(15.dp))

            // "청구서 발송하기" 버튼
            Button(
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                onClick = {
                    Toast
                        .makeText(context, "발송 완료~", Toast.LENGTH_SHORT)
                        .show()
                },
                enabled = isButtonEnabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                )
            ) {
                Text("청구서 발송하기", fontSize = 18.sp, color = Color.White)
            }
        }
    }

    // 팝업 UI (TextField 입력 창)
    if (showTextField1) {
        InputPopup(
            title = "청구대상 입력",
            initialText = text1,
            onConfirm = { text1 = it; showTextField1 = false },
            onDismiss = { showTextField1 = false }
        )
    }
    if (showTextField2) {
        InputPopup(
            title = "휴대전화번호 입력",
            initialText = text2,
            onConfirm = { text2 = it; showTextField2 = false },
            onDismiss = { showTextField2 = false }
        )
    }
    if (showTextField3) {
        InputPopup(
            title = "청구항목 입력",
            initialText = text3,
            onConfirm = { text3 = it; showTextField3 = false },
            onDismiss = { showTextField3 = false }
        )
    }
    if (showTextField4) {
        InputPopup(
            title = "청구금액 입력",
            initialText = text4,
            onConfirm = { text4 = it; showTextField4 = false },
            onDismiss = { showTextField4 = false }
        )
    }
}

/**
 * 팝업 UI Composable
 */
@Composable
fun InputPopup(
    title: String,
    initialText: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var tempText by remember { mutableStateOf(initialText) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .background(Color.White, shape = RoundedCornerShape(8.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = tempText,
                onValueChange = { tempText = it },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    border = BorderStroke(0.01.dp, Color.Black),
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .width(100.dp)
                        .height(40.dp),
                    onClick = onDismiss
                ) {
                    Text("취소")
                }
                Button(
                    border = BorderStroke(0.01.dp, Color.Black),
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .width(100.dp)
                        .height(40.dp),
                    onClick = {
                        onConfirm(tempText)
                    }
                ) {
                    Text("확인")
                }
            }
        }
    }
}
