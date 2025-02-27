package com.example.cv2project

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
    var announcements by remember { mutableStateOf<List<Announcement>>(emptyList()) }

    // Firebase에서 공지사항 가져오기
    LaunchedEffect(Unit) {
        announcementDb.getAnnouncements { loadedAnnouncements ->
            announcements = loadedAnnouncements
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 상단 바
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
            Image(
                painter = painterResource(id = R.drawable.announcement1),
                contentDescription = "공지사항",
                modifier = Modifier.size(150.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.add),
                contentDescription = "추가",
                modifier = Modifier
                    .padding(end = 15.dp)
                    .size(30.dp)
                    .clickable { navController.navigate("addAnnouncement") }
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
                            },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0))
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
    var selectedTab by remember { mutableStateOf("지금작성") }
    val tabs = listOf("지금작성", "임시저장", "예약작성")

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
            horizontalArrangement = Arrangement.Start
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
            Spacer(modifier = Modifier.weight(0.9f))
            Image(
                painter = painterResource(id = R.drawable.announcement3),
                contentDescription = "공지사항 작성",
                modifier = Modifier.size(200.dp)
            )
            Spacer(modifier = Modifier.weight(1.1f))
        }
        Spacer(modifier = Modifier.height(30.dp))

        // 탭
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEach { tab ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { selectedTab = tab }
                ) {
                    Text(
                        text = tab,
                        fontSize = 20.sp,
                        fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab == tab) Color.Black else Color(0xFF828282),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Box(
                        modifier = Modifier
                            .height(1.dp)
                            .width(80.dp)
                            .background(if (selectedTab == tab) Color(0xFFFF7800) else Color.Transparent)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(15.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("공지사항 제목") },
            modifier = Modifier
                .width(300.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("공지사항 내용") },
            modifier = Modifier
                .width(300.dp)
                .height(300.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))

        // 저장 버튼
        OutlinedButton(
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp),
            border = BorderStroke(2.dp, Color(0xFF4786FF)), // 아웃라인 색을 #4786FF로 설정
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
        ) {
            Text("공지사항 추가")
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
            Image(
                painter = painterResource(id = R.drawable.announcement2),
                contentDescription = "공지사항 내용",
                modifier = Modifier.size(200.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.trash),
                contentDescription = "삭제",
                modifier = Modifier
                    .padding(end = 15.dp)
                    .size(25.dp)
                    .clickable { showDialog = true }
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
                title = { Text("공지사항 삭제", fontWeight = FontWeight.Bold) },
                text = { Text("내용이 영구적으로 삭제됩니다.\n삭제하시겠습니까?") },
                confirmButton = {
                    Button(
                        onClick = {
                            announcementDb.deleteAnnouncement(announcement.id) { success ->
                                if (success) {
                                    navController.popBackStack()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4786FF))
                    ) {
                        Text("삭제", color = Color.White)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4786FF))
                    ) {
                        Text("취소", color = Color.White)
                    }
                }
            )
        }
    }
}