package com.example.cv2project

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cv2project.firebase.NoticeDatabase
import com.example.cv2project.firebase.StudentDatabase
import com.example.cv2project.models.Comment
import com.example.cv2project.models.Notice
import com.example.cv2project.models.Student
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 네비게이션으로 사용할 Composable
 * - route 예: "notice"
 */
@Composable
fun NoticeScreen(navController: NavController, noticeDb: NoticeDatabase) {
    var notices by remember { mutableStateOf<List<Notice>>(emptyList()) }

    LaunchedEffect(Unit) {
        noticeDb.getNotices { fetchedNotices ->
            notices = fetchedNotices
        }
    }

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
                painter = painterResource(id = R.drawable.notice1),
                contentDescription = "알림장",
                modifier = Modifier.size(150.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.add),
                contentDescription = "추가",
                modifier = Modifier
                    .padding(end = 15.dp)
                    .size(30.dp)
                    .clickable {
                        navController.navigate("addNotice")
                    }
            )
        }

        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 20.dp)
        ) {
            if (notices.isEmpty()) {
                Text("등록된 알림이 없습니다.", modifier = Modifier.padding(16.dp))
            } else {
                notices.forEach { notice ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                navController.navigate(
                                    "detailNotice?id=${notice.id}&title=${notice.title}" +
                                            "&content=${notice.content}" +
                                            "&studentName=${notice.studentName}" +
                                            "&date=${notice.date}"
                                )
                            }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("📅 ${notice.date}")
                            Text("🎓 학생: ${notice.studentName}")
                            Text("📝 제목: ${notice.title}")
                            Text("📄 내용: ${notice.content}")
                        }
                    }
                }
            }
        }
    }
}

/**
 * 알림장 작성 Composable
 * - Navigation Graph에서 route를 "addNotice"로 등록하고,
 *   navController.navigate("addNotice")로 화면 전환
 */
@Composable
fun AddNoticeScreen(
    navController: NavController,
    studentDb: StudentDatabase,
    noticeDb: NoticeDatabase
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var students by remember { mutableStateOf<List<Student>>(emptyList()) }
    var selectedStudent by remember { mutableStateOf<Student?>(null) }
    val todayDate = getTodayDate() // 오늘 날짜 가져오기
    var selectedTab by remember { mutableStateOf("지금전송") }
    val tabs = listOf("지금전송", "임시저장", "예약전송")


    // Firebase에서 전체 학생 목록 불러오기
    LaunchedEffect(Unit) {
        studentDb.loadAllStudents { loadedStudents ->
            students = loadedStudents
        }
    }

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
            Spacer(modifier = Modifier.weight(0.9f)) // 중앙 정렬을 위한 Spacer
            Image(
                painter = painterResource(id = R.drawable.notice2),
                contentDescription = "알림장 작성",
                modifier = Modifier.size(200.dp)
            )
            Spacer(modifier = Modifier.weight(1.1f))
        }
        Spacer(modifier = Modifier.height(15.dp))

        // 탭 UI
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
        Spacer(modifier = Modifier.height(10.dp))

        // 학생 선택
        Text(
            text = "학생 선택",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )

        Spacer(modifier = Modifier.height(5.dp))
        students.forEach { student ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedStudent = student }
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = student.name,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                if (selectedStudent == student) {
                    Text("✔️")
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))

        // 알림 제목 입력
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("알림장 제목") },
            modifier = Modifier
                .width(300.dp)
        )
        Spacer(modifier = Modifier.height(5.dp))
        // 알림 내용 입력
        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("알림장 내용") },
            modifier = Modifier
                .width(300.dp)
                .height(300.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedButton(
            onClick = {
                if (title.isNotEmpty() && content.isNotEmpty() && selectedStudent != null) {
                    val newNotice = Notice(
                        id = "", // Firebase에서 key를 자동 생성하도록 빈 값 설정
                        title = title,
                        content = content,
                        studentName = selectedStudent!!.name,
                        date = todayDate
                    )

                    // Firebase에 저장
                    noticeDb.saveNotice(newNotice) { success ->
                        if (success) navController.popBackStack()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 30.dp),
            border = BorderStroke(2.dp, Color(0xFF4786FF)), // 아웃라인 색을 #4786FF로 설정
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black) // 텍스트 색은 검정색으로 설정
        ) {
            Text("알림장 추가")
        }
    }
}

@Composable // 알림장 내용
fun DetailNoticeScreen(
    navController: NavController,
    notice: Notice,
    noticeDb: NoticeDatabase
) {
    var showDialog by remember { mutableStateOf(false) }
    var showCommentDialog by remember { mutableStateOf(false) }
    var comments by remember { mutableStateOf<List<Comment>>(emptyList()) }
    var newComment by remember { mutableStateOf("") }

    // Firebase에서 해당 알림의 댓글 가져오기
    LaunchedEffect(Unit) {
        noticeDb.getComments(notice.id) { fetchedComments ->
            comments = fetchedComments
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
                painter = painterResource(id = R.drawable.notice3),
                contentDescription = "알림장 내용",
                modifier = Modifier.size(200.dp)
            )
            // 알림장 삭제 버튼
            Image(
                painter = painterResource(id = R.drawable.trash),
                contentDescription = "Delete notice",
                modifier = Modifier
                    .padding(end = 15.dp)
                    .size(30.dp)
                    .clickable { showDialog = true }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Spacer(modifier = Modifier.size(10.dp))
            Text(text = notice.title, fontSize = 30.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.size(20.dp))
            Text(text = notice.content, fontSize = 18.sp)
            Spacer(modifier = Modifier.size(50.dp))
            Text(text = "학생: ${notice.studentName}", fontSize = 14.sp, color = Color.Gray)
            Text(text = "작성 날짜: ${notice.date}", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(20.dp))

        }
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
                .height(250.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("💬 댓글 ${comments.size}", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(15.dp))

            if (comments.isEmpty()) {
                Text("등록된 댓글이 없습니다.")
            } else {
                comments.forEachIndexed { index, comment ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("${comment.author}  ${comment.timestamp}")
                            Text("댓글 : ${comment.text}")
                            Divider()
                            Spacer(modifier = Modifier.size(3.dp))
                        }
                        Image(
                            painter = painterResource(R.drawable.trash),
                            contentDescription = "댓글 삭제",
                            modifier = Modifier
                                .size(20.dp)
                                .clickable {
                                    Log.d(
                                        "Firebase",
                                        "🔥 댓글 삭제 버튼 클릭됨: commentId=${comment.id}"
                                    ) // ✅ 삭제 전 로그
                                    noticeDb.deleteComment(notice.id, comment.id) { success ->
                                        if (success) {
                                            // 🔥 Firebase에서 데이터 다시 가져와서 최신 상태 유지
                                            noticeDb.getComments(notice.id) { updatedComments ->
                                                comments = updatedComments
                                            }
                                        }
                                    }
                                }
                        )
                    }
                }
            }
        }

        // 댓글 작성 버튼
        Button(
            onClick = { showCommentDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4786FF))
        ) {
            Text("댓글 작성", color = Color.White)
        }

        // 댓글 작성 팝업 다이얼로그
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("알림장 삭제", fontWeight = FontWeight.Bold) },
                text = { Text("내용이 영구적으로 삭제됩니다.\n삭제하시겠습니까?") },
                confirmButton = {
                    Button(
                        onClick = {
                            noticeDb.deleteNotice(notice.id) { success ->
                                if (success) navController.popBackStack()
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
        // 댓글 작성 팝업 다이얼로그
        if (showCommentDialog) {
            AlertDialog(
                onDismissRequest = { showCommentDialog = false },
                title = { Text("댓글 작성",fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        OutlinedTextField(
                            value = newComment,
                            onValueChange = { newComment = it },
                            placeholder = { Text("댓글을 입력하세요.") },
                            textStyle = TextStyle(color = Color.Black),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newComment.isNotEmpty()) {
                                val timestamp =
                                    SimpleDateFormat(
                                        "yyyy-MM-dd HH:mm",
                                        Locale.getDefault()
                                    ).format(Date())
                                val comment = Comment(
                                    id = "",
                                    author = "사용자",
                                    text = newComment,
                                    timestamp = timestamp
                                )
                                noticeDb.addComment(notice.id, comment) { success ->
                                    if (success) {
                                        // 🔥 Firebase에서 데이터 다시 가져와서 최신 상태 유지
                                        noticeDb.getComments(notice.id) { updatedComments ->
                                            comments = updatedComments
                                            newComment = ""
                                            showCommentDialog = false
                                        }
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4786FF))
                    ) {
                        Text("등록", color = Color.White)
                    }
                },
                dismissButton = {
                    Button(onClick = { showCommentDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4786FF))
                    ) {
                        Text("취소", color = Color.White)
                    }
                }
            )
        }
    }
}