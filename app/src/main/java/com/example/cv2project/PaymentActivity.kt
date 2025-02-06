package com.example.cv2project

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cv2project.ui.theme.CV2ProjectTheme

class PaymentActivity: ComponentActivity() {
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
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(10.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.1f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(color = Color.White),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "원비 결제",
                    color = Color.Black,
                    fontSize = 30.sp,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 10.dp)
                )
                Image(
                    painter = painterResource(R.drawable.pen),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 20.dp)
                        .size(30.dp)
                        .clickable {}
                )
                Image(
                    painter = painterResource(R.drawable.x),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 15.dp)
                        .size(20.dp)
                        .clickable { context?.finish() }
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 20.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                Text("대상 정보 입력하기 *", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("청구대상", fontSize = 10.sp)
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .height(60.dp)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .border(
                            width = 1.dp,
                            color = Color.Gray
                        ),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text("청구대상 입력 (최대 20자)", color = Color.Gray, fontSize = 15.sp)
                }
                Text("반드시 본인의 실명을 입력하세요.", color = Color.Gray)
                Spacer(modifier = Modifier.size(10.dp))
                Text("휴대전화번호", fontSize = 10.sp)

                Column(
                    modifier = Modifier.fillMaxWidth()
                        .height(60.dp)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .border(
                            width = 1.dp,
                            color = Color.Gray
                        ),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text("휴대전화번호 입력", color = Color.Gray, fontSize = 15.sp)
                }
                Text("반드시 본인 휴대전화번호를 입력하세요.", color = Color.Gray)
// ---------------------------------------------------------------------------------------------------------------------
                Spacer(modifier = Modifier.size(15.dp))
                Text("청구 정보 입력하기 *", fontSize = 20.sp, fontWeight = FontWeight.Bold)

                Text("청구항목", fontSize = 15.sp)
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .height(60.dp)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .border(
                            width = 1.dp,
                            color = Color.Gray
                        ),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text("청구항목 입력 (최대 20자)", color = Color.Gray, fontSize = 15.sp)
                }
                Spacer(modifier = Modifier.size(10.dp))
                Text("청구금액 (원)", fontSize = 10.sp)

                Column(
                    modifier = Modifier.fillMaxWidth()
                        .height(60.dp)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .border(
                            width = 1.dp,
                            color = Color.Gray
                        ),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text("청구금액 입력", color = Color.Gray, fontSize = 17.sp)
                }
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .height(120.dp)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(color = Color.LightGray),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Column(
                        modifier = Modifier.weight(0.5f)
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
                            Text("개인정보 수집이용 및 마케팅 정보 수신동의\n(필수)", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        }
                    }
                    Divider(thickness = 1.dp)
                    Column(
                        modifier = Modifier.weight(0.5f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("개인정보 수집동의 안내", fontWeight = FontWeight.SemiBold, fontSize = 10.sp, modifier = Modifier.weight(1f))
                            Icon(
                                painter = painterResource(R.drawable.red),
                                contentDescription = null,
                                modifier = Modifier.padding(end = 5.dp)
                                    .size(30.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.size(10.dp))
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .height(60.dp)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(color = Color.LightGray),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("청구서 발송 시, 문자로 전송됩니다.", fontSize = 10.sp, color = Color.Gray)
                }
                Button(
                    modifier = Modifier.fillMaxWidth()
                        .height(60.dp)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    onClick = {}
                ) {
                    Text("청구서 발송하기", fontSize = 18.sp, color = Color.White)
                }
            }
        }
    }
}