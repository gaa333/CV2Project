package com.example.cv2project

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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

            Text("알림장", fontSize = 25.sp, color = Color.White)

            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 15.dp)
                    .size(30.dp)
                    .clickable { navController.navigate("addNotice") },
                tint = Color.White
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

    // Firebase에서 전체 학생 목록 불러오기
    LaunchedEffect(Unit) {
        studentDb.loadAllStudents { loadedStudents ->
            students = loadedStudents
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
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
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text("저장", color = Color.White)
        }
    }
}

/**
 * 알림장 상세화면 (Composable)
 */
@Composable
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

            Text("알림장 내용", fontSize = 25.sp, color = Color.White)

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
            Text("💬 댓글 ${comments.size}")
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
                            painter = painterResource(R.drawable.delete),
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
            modifier = Modifier.padding(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text("댓글 작성", color = Color.White)
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("알림장 삭제") },
                text = { Text("삭제하시겠습니까?") },
                confirmButton = {
                    Button(
                        onClick = {
                            noticeDb.deleteNotice(notice.id) { success ->
                                if (success) navController.popBackStack()
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
        // 댓글 작성 팝업 다이얼로그
        if (showCommentDialog) {
            AlertDialog(
                onDismissRequest = { showCommentDialog = false },
                title = { Text("댓글 작성") },
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
                        }) {
                        Text("등록")
                    }
                },
                dismissButton = {
                    Button(onClick = { showCommentDialog = false }) {
                        Text("취소")
                    }
                }
            )
        }
    }
}