package com.example.cv2project

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cv2project.ui.theme.CV2ProjectTheme

class StudentClassListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CV2ProjectTheme {
                ClassListScreen()
            }
        }
    }
}

@Composable
fun ClassListScreen() {
    val context = LocalContext.current as? Activity
    val classList = remember { listOf("6세반", "7세반", "8세반", "9세반") } // 반 목록

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row( // ✅ Row로 감싸서 한 줄로 정렬
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "뒤로가기",
                modifier = Modifier
                    .size(25.dp)
                    .clickable { context?.finish() }
            )
            Spacer(modifier = Modifier.width(16.dp)) // 아이콘과 텍스트 사이 여백 추가

            Text(
                text = "반 목록",
                fontSize = 24.sp,
                modifier = Modifier.weight(1f) // 텍스트를 가운데 정렬하기 위해 weight 사용
            )
        }

        classList.forEach { className ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable {
                        val intent = Intent(context, StudentManagementActivity::class.java)
                        intent.putExtra("class_name", className)
                        context?.startActivity(intent)
                    }
            ) {
                Text(
                    text = className,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
//            Image(
//                painter = painterResource(id = R.drawable.student2),
//                contentDescription = "student2",
//                Modifier.fillMaxWidth()
//                    .padding(horizontal = 16.dp)
//            )
        }
    }
}

