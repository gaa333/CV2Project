package com.example.cv2project

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cv2project.preferences.Student
import com.example.cv2project.preferences.StudentPreferences
import com.example.cv2project.ui.theme.CV2ProjectTheme

class StudentManagementActivity : ComponentActivity() {
    private lateinit var studentPrefs: StudentPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        studentPrefs = StudentPreferences(this)
        val selectedClassName = intent.getStringExtra("class_name") ?: "반 이름 없음"
        setContent {
            CV2ProjectTheme {
                StudentManagementScreen(studentPrefs, selectedClassName)
            }
        }
    }
}

// 학생 프로필 목록
@Composable
fun StudentManagementScreen(studentPrefs: StudentPreferences, selectedClassName: String) {
    val context = LocalContext.current as? Activity
    var isAddingStudent by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
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
            Text(
                text = "학생 프로필", //"$selectedClassName 반"
                color = Color.White,
                fontSize = 25.sp,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 15.dp)
                    .size(25.dp)
                    .clickable {
                        isAddingStudent = true
                    },
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.height(15.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(id = R.drawable.profile2),
                contentDescription = "검색",
                modifier = Modifier.padding(start = 5.dp, end = 5.dp)
            )
            Spacer(modifier = Modifier.height(15.dp))

            Image(
                painter = painterResource(id = R.drawable.profile3),
                contentDescription = "음바페",
                modifier = Modifier
                    .clickable {
                        val intent = Intent(context, StudentDetailActivity::class.java)
                        intent.putExtra("student_name", "음바페")
                        context?.startActivity(intent)
                    }
            )
            Image(
                painter = painterResource(id = R.drawable.profile4),
                contentDescription = "손흥민")
            Image(
                painter = painterResource(id = R.drawable.profile5),
                contentDescription = "호날두")
            Image(
                painter = painterResource(id = R.drawable.profile6),
                contentDescription = "메시")
            Image(
                painter = painterResource(id = R.drawable.profile7),
                contentDescription = "네이마르")
        }
    }
}