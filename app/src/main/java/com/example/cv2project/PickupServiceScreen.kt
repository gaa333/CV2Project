package com.example.cv2project

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

/**
 * 픽업 서비스 화면 (Composable)
 * - Navigation Graph에서 "pickupService"로 등록해두고 navController.navigate("pickupService")로 전환.
 */
@Composable
fun PickupServiceScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 상단 바(뒤로가기, 공유 아이콘, 제목)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(color = Color.Black),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 뒤로가기 버튼
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "뒤로가기",
                modifier = Modifier
                    .padding(start = 15.dp)
                    .size(25.dp)
                    .clickable {
                        // 네비게이션으로 뒤로가기
                        navController.popBackStack()
                    },
                tint = Color.White
            )
            Text(
                text = "픽업 서비스",
                fontSize = 25.sp,
                color = Color.White
            )
            // 공유 아이콘 (현재 동작 없음)
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "공유",
                modifier = Modifier
                    .padding(end = 15.dp)
                    .size(25.dp),
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.weight(0.1f))

        // 지도 이미지
        Image(
            painter = painterResource(id = R.drawable.pikup),
            contentDescription = "지도",
            Modifier.fillMaxSize()
        )
        Spacer(modifier = Modifier.weight(0.15f))
    }
}
