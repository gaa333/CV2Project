package com.example.cv2project

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cv2project.ui.theme.CV2ProjectTheme


class PickupServiceActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CV2ProjectTheme {
                PickupServiceScreen()
            }
        }
    }
}

// 픽업 서비스
@Composable
fun PickupServiceScreen() {
    val context = LocalContext.current as? Activity
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("픽업 서비스", fontSize = 40.sp)
            Image(
                painter = painterResource(R.drawable.x),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 20.dp)
                    .size(20.dp)
                    .clickable { context?.finish() }
            )

        }

        Spacer(modifier = Modifier.weight(0.1f))

        Image(
            painter = painterResource(id = R.drawable.pikup),
            contentDescription = "지도",
            Modifier.fillMaxSize()
        )

        Spacer(modifier = Modifier.weight(0.1f))

    }

}