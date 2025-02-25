package com.example.cv2project

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cv2project.preferences.Comment
import com.example.cv2project.preferences.CommentPreferences
import com.example.cv2project.preferences.Notice
import com.example.cv2project.preferences.NoticePreferences
import com.example.cv2project.preferences.Student
import com.example.cv2project.preferences.StudentPreferences
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 네비게이션으로 사용할 Composable
 * - route 예: "notice"
 */
@Composable
fun NoticeScreen(navController: NavController) {
    val context = LocalContext.current
    val noticePrefs = remember { NoticePreferences(context) }
    val notices = remember { mutableStateOf(noticePrefs.loadNotices()) }

    // 다른 화면에서 돌아올 때마다 목록을 새로고침하고 싶다면
    LaunchedEffect(Unit) {
        notices.value = noticePrefs.loadNotices()
    }

    // Composable UI
    NoticeContent(
        navController = navController,
        notices = notices,
        noticePrefs = noticePrefs
    )
}

/**
 * 실제 UI 구성을 담당하는 함수
 */
@Composable
fun NoticeContent(
    navController: NavController,
    notices: MutableState<List<Notice>>,
    noticePrefs: NoticePreferences
) {
    // UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.systemBars.asPaddingValues()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 상단 Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(color = Color.Black),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 뒤로 가기 버튼: navController.popBackStack() 사용
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "뒤로가기",
                modifier = Modifier
                    .padding(start = 15.dp)
                    .size(25.dp)
                    .clickable {
                        navController.popBackStack() // 뒤로가기
                    },
                tint = Color.White
            )

            Text(
                "알림장",
                fontSize = 25.sp,
                color = Color.White
            )

            // 알림 추가 버튼
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 15.dp)
                    .size(30.dp)
                    .clickable {
                        // AddNoticeActivity 이동
                        navController.navigate("addNotice")
                    },
                tint = Color.White
            )
        }

        // 알림 목록
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 20.dp)
        ) {
            if (notices.value.isEmpty()) {
                Text("등록된 알림이 없습니다.", modifier = Modifier.padding(16.dp))
            } else {
                notices.value.forEach { notice ->
                    androidx.compose.material3.Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                navController.navigate(
                                    "detailNotice?title=${notice.title}" +
                                            "&content=${notice.content}" +
                                            "&studentName=${notice.studentName}" +
                                            "&date=${notice.date}" +
                                            "&noticeId=${notice.title}-${notice.date}" // ✅ 원하는 형식으로 noticeId 생성
                                )
                            },
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
    studentPrefs: StudentPreferences,
    noticePrefs: NoticePreferences
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var students by remember { mutableStateOf(studentPrefs.loadAllStudents()) }
    var selectedStudent by remember { mutableStateOf<Student?>(null) }
    val todayDate = getTodayDate() // 오늘 날짜 (사용자 정의 함수)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .padding(WindowInsets.systemBars.asPaddingValues()),
        verticalArrangement = Arrangement.Center
    ) {
        Text("알림장 작성", fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))

        Text("학생 선택", fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))

        // 학생 목록
        students.forEach { student ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedStudent = student }
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = student.name)
                if (selectedStudent == student) {
                    Text("✔️")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("알림 제목") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("알림 내용") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (title.isNotEmpty() && content.isNotEmpty() && selectedStudent != null) {
                    val newNotice = Notice(
                        title = title,
                        content = content,
                        studentName = selectedStudent!!.name,
                        date = todayDate
                    )
                    val updatedNotices = noticePrefs.loadNotices().toMutableList()
                    updatedNotices.add(newNotice)
                    noticePrefs.saveNotices(updatedNotices)

                    // 완료 후 뒤로가기
                    navController.popBackStack()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text("저장", color = Color.White)
        }
    }
}

/**
 * 알림장 상세화면 (Composable)
 *
 * Navigation Graph에서 "detailNotice"로 등록 후,
 * navController.navigate("detailNotice?title=...&content=...&studentName=...&date=...")
 * 형태로 전달하거나, 다른 방식을 통해 파라미터를 주입할 수 있습니다.
 */
@Composable
fun DetailNoticeScreen(
    navController: NavController,
    title: String,
    content: String,
    studentName: String,
    date: String,
    noticeId: String, // 고유 알림장 id (ex: "$title-$date")
    commentPrefs: CommentPreferences,
    noticePrefs: NoticePreferences
) {
    // 댓글 목록 및 새로운 댓글 텍스트
    val comments = remember { mutableStateOf(commentPrefs.loadComments(noticeId)) }
    val newComment = remember { mutableStateOf("") }

    // 삭제 확인 다이얼로그 노출 여부
    var showDialog by remember { mutableStateOf(false) }
    // 댓글 작성 팝업
    val showCommentDialog = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.systemBars.asPaddingValues()),
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
            // 뒤로가기 (navController.popBackStack())
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack,
                contentDescription = "뒤로가기",
                modifier = Modifier
                    .padding(start = 15.dp)
                    .size(25.dp)
                    .clickable {
                        navController.popBackStack()
                    },
                tint = Color.White
            )

            Text(
                "알림장 내용",
                color = Color.White,
                fontSize = 25.sp
            )

            // 알림장 삭제 버튼
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Delete,
                contentDescription = "Delete notice",
                modifier = Modifier
                    .padding(end = 15.dp)
                    .size(25.dp)
                    .clickable { showDialog = true },
                tint = Color.White
            )
        }

        // 삭제 확인 다이얼로그
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("알림장 삭제") },
                text = { Text("내용이 영구적으로 삭제됩니다.\n삭제하시겠습니까?") },
                confirmButton = {
                    Button(onClick = {
                        // 1) noticePreferences.loadAndDeleteNotice(noticeId) 호출
                        noticePrefs.loadAndDeleteNotice(noticeId)

                        // 2) navController.popBackStack() 으로 이전 화면 복귀
                        navController.popBackStack()
                    }) {
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

        // 알림장 상세 내용
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Spacer(modifier = Modifier.size(10.dp))
            Text(text = title, fontSize = 30.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = content, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(50.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "$studentName 학생",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "작성 날짜: $date",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Divider(Modifier.width(1.dp))
        }

        // 댓글 표시 영역
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
                .height(300.dp)
        ) {
            Text("💬 댓글 ${comments.value.size}")
            Spacer(modifier = Modifier.height(15.dp))

            if (comments.value.isEmpty()) {
                Text("등록된 댓글이 없습니다.")
            } else {
                comments.value.forEachIndexed { index, comment ->
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
                            painter = painterResource(R.drawable.delete),
                            contentDescription = "댓글 삭제",
                            modifier = Modifier
                                .size(20.dp)
                                .clickable {
                                    val updatedComments = comments.value.toMutableList()
                                    updatedComments.removeAt(index)
                                    comments.value = updatedComments
                                    commentPrefs.saveComments(noticeId, updatedComments)
                                }
                        )
                    }
                }
            }
        }

        // 댓글 작성 버튼
        Button(
            onClick = { showCommentDialog.value = true },
            modifier = Modifier.padding(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text("댓글 작성", color = Color.White)
        }
    }

    // 댓글 작성 팝업 다이얼로그
    if (showCommentDialog.value) {
        AlertDialog(
            onDismissRequest = { showCommentDialog.value = false },
            title = { Text("댓글 작성") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newComment.value,
                        onValueChange = { newComment.value = it },
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
                        if (newComment.value.isNotEmpty()) {
                            val timestamp =
                                SimpleDateFormat("MM월 dd일 HH:mm", Locale.getDefault()).format(
                                    Date()
                                )
                            val newCommentData = Comment(
                                author = "사용자",
                                text = newComment.value,
                                timestamp = timestamp
                            )
                            val updatedComments = comments.value + newCommentData
                            comments.value = updatedComments
                            commentPrefs.saveComments(noticeId, updatedComments)
                            newComment.value = ""
                            showCommentDialog.value = false
                        }
                    }
                ) {
                    Text("등록")
                }
            },
            dismissButton = {
                Button(onClick = { showCommentDialog.value = false }) {
                    Text("취소")
                }
            }
        )
    }
}