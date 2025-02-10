package com.example.cv2project

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cv2project.ui.theme.CV2ProjectTheme

class PaymentActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CV2ProjectTheme {
                PaymentScreen()
            }
        }
    }
}

// 원비 결제
@Composable
fun PaymentScreen() {
    val context = LocalContext.current as? Activity
    var checked by remember { mutableStateOf(false) }

    var showTextField1 by remember { mutableStateOf(false) }
    var showTextField2 by remember { mutableStateOf(false) }
    var showTextField3 by remember { mutableStateOf(false) }
    var showTextField4 by remember { mutableStateOf(false) }

    var text1 by remember { mutableStateOf("") }
    var text2 by remember { mutableStateOf("") }
    var text3 by remember { mutableStateOf("") }
    var text4 by remember { mutableStateOf("") }

    val isAllFieldsFilled = text1.isNotEmpty() && text2.isNotEmpty() && text3.isNotEmpty() && text4.isNotEmpty()
    val isButtonEnabled = isAllFieldsFilled && checked

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                        .clickable { context?.finish() },
                    tint = Color.White
                )
                Text("원비 결제",color = Color.White,fontSize = 25.sp)
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "공유",
                    modifier = Modifier
                        .padding(end = 15.dp)
                        .size(25.dp),
                    tint = Color.White
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(15.dp)
                    .padding(bottom = 20.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                Text("대상 정보 입력하기 *", fontSize = 20.sp, fontWeight = FontWeight.Bold)

                // ✅ 청구대상 클릭 시 팝업 활성화
                Text("청구대상", fontSize = 10.sp)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(10.dp)
                        .border(width = 1.dp, color = Color.Gray)
                        .clickable { showTextField1 = true },
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(text1.ifEmpty { "청구대상 입력 (최대 20자)" }, color = Color.Gray, fontSize = 15.sp)
                }
                Text("반드시 본인의 실명을 입력하세요.", color = Color.Gray)

                Spacer(modifier = Modifier.size(10.dp))
                Text("휴대전화번호", fontSize = 10.sp)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(10.dp)
                        .border(width = 1.dp, color = Color.Gray)
                        .clickable { showTextField2 = true },
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(text2.ifEmpty { "휴대전화번호 입력" }, color = Color.Gray, fontSize = 15.sp)
                }
                Text("반드시 본인 휴대전화번호를 입력하세요.", color = Color.Gray)

                // ✅ 청구항목 클릭 시 팝업 활성화
                Spacer(modifier = Modifier.size(15.dp))
                Text("청구 정보 입력하기 *", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("청구항목", fontSize = 15.sp)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(10.dp)
                        .border(width = 1.dp, color = Color.Gray)
                        .clickable { showTextField3 = true },
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(text3.ifEmpty { "청구항목 입력 (최대 20자)" }, color = Color.Gray, fontSize = 15.sp)
                }

                // ✅ 청구금액 클릭 시 팝업 활성화
                Spacer(modifier = Modifier.size(10.dp))
                Text("청구금액 (원)", fontSize = 10.sp)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(10.dp)
                        .border(width = 1.dp, color = Color.Gray)
                        .clickable { showTextField4 = true },
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(text4.ifEmpty { "청구금액 입력" }, color = Color.Gray, fontSize = 17.sp)
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(10.dp)
                        .background(color = Color.LightGray),
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
                                modifier = Modifier.size(30.dp)
                            )
                            Text(
                                "개인정보 수집이용 및 마케팅 정보 수신동의 (필수)",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
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
                                fontSize = 10.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                painter = painterResource(R.drawable.red),
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
                        .height(60.dp)
                        .padding(10.dp)
                        .background(color = Color.LightGray),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("청구서 발송 시, 문자로 전송됩니다.", fontSize = 10.sp, color = Color.Gray)
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(10.dp),
                    onClick = {
                        Toast.makeText(context, "발송 완료 :)", Toast.LENGTH_SHORT).show()
                    },
                    enabled = isButtonEnabled,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black // Set the button background color to black
                    )
                ) {
                    Text("청구서 발송하기", fontSize = 18.sp, color = Color.White) // Set the text color to white
                }

            }
        }
    }

    // ✅ 팝업 UI (TextField 입력 창)
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

// ✅ 팝업 UI Composable
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
                    }) {
                    Text("업로드")
                }
            }
        }
    }
}
