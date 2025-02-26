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
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = R.drawable.back),
                contentDescription = "뒤로가기",
                modifier = Modifier
                    .padding(start = 15.dp)
                    .size(25.dp)
                    .clickable {
                        navController.popBackStack()
                    }
            )
            Spacer(modifier = Modifier.weight(0.8f))
            Image(
                painter = painterResource(id = R.drawable.pickup1),
                contentDescription = "픽업서비스",
                modifier = Modifier.size(180.dp)
            )
            Spacer(modifier = Modifier.weight(1.2f))
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
