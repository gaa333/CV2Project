package com.example.cv2project

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cv2project.firebase.AnnouncementDatabase
import com.example.cv2project.models.Announcement
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 공지사항 Composable
 * 네비게이션에서 route를 "announcement"로 등록해서 사용
 */
@Composable
fun AnnouncementScreen(navController: NavController, announcementDb: AnnouncementDatabase) {
    val context = LocalContext.current
    var announcements by remember { mutableStateOf<List<Announcement>>(emptyList()) }

    // Firebase에서 공지사항 가져오기
    LaunchedEffect(Unit) {
        announcementDb.getAnnouncements { loadedAnnouncements ->
            announcements = loadedAnnouncements
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
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
                    .clickable { navController.popBackStack() },
                tint = Color.White
            )

            Text("공지사항", color = Color.White, fontSize = 25.sp)

            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 15.dp)
                    .size(30.dp)
                    .clickable { navController.navigate("addAnnouncement") },
                tint = Color.White
            )
        }

        // 공지사항 목록
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 20.dp)
        ) {
            if (announcements.isEmpty()) {
                Text(
                    "등록된 공지가 없습니다.",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                announcements.forEach { announcement ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                navController.navigate(
                                    "detailAnnouncement?id=${announcement.id}" +
                                            "&title=${announcement.title}" +
                                            "&content=${announcement.content}" +
                                            "&date=${announcement.date}"
                                )
                            }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = announcement.title,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = announcement.content, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = announcement.date, fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddAnnouncementScreen(navController: NavController, announcementDb: AnnouncementDatabase) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    val currentDate by remember {
        mutableStateOf(
            SimpleDateFormat(
                "yyyy-MM-dd HH:mm",
                Locale.getDefault()
            ).format(Date())
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("공지사항 추가", fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("제목 입력") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("내용 입력") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (title.isNotEmpty() && content.isNotEmpty()) {
                    val newAnnouncement = Announcement(
                        id = "",  // ✅ Firebase에서 자동 생성되므로 빈 문자열로 초기화
                        title = title,
                        content = content,
                        date = currentDate
                    )
                    announcementDb.saveAnnouncement(newAnnouncement) { success ->
                        if (success) navController.popBackStack()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text("저장")
        }
    }
}

@Composable
fun DetailAnnouncementScreen(
    navController: NavController,
    announcement: Announcement,
    announcementDb: AnnouncementDatabase
) {
    var showDialog by remember { mutableStateOf(false) }

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
                    .clickable { navController.popBackStack() },
                tint = Color.White
            )

            Text("공지사항 내용", color = Color.White, fontSize = 25.sp)

            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "삭제",
                modifier = Modifier
                    .padding(end = 15.dp)
                    .size(25.dp)
                    .clickable { showDialog = true },
                tint = Color.White
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Spacer(modifier = Modifier.size(10.dp))
            Text(text = announcement.title, fontSize = 30.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.size(20.dp))
            Text(text = announcement.content, fontSize = 18.sp)
            Spacer(modifier = Modifier.size(50.dp))
            Text(text = "작성 날짜: ${announcement.date}", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(20.dp))
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("공지사항 삭제") },
                text = { Text("삭제하시겠습니까?") },
                confirmButton = {
                    Button(
                        onClick = {
                            announcementDb.deleteAnnouncement(announcement.id) { success ->
                                if (success) {
                                    navController.popBackStack()
                                }
                            }
                        }
                    ) {
                        Text("삭제")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("취소")
                    }
                }
            )
        }
    }
}