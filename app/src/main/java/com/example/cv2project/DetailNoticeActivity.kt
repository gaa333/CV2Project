package com.example.cv2project

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cv2project.preferences.Comment
import com.example.cv2project.preferences.CommentPreferences
import com.example.cv2project.ui.theme.CV2ProjectTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailNoticeActivity : ComponentActivity() {
    @SuppressLint("RememberReturnType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 전달된 데이터 받기
        val title = intent.getStringExtra("title") ?: "제목 없음"
        val content = intent.getStringExtra("content") ?: "내용 없음"
        val studentName = intent.getStringExtra("studentName") ?: "이름 없음"
        val date = intent.getStringExtra("date") ?: "날짜 없음"

        val noticeId = "$title-$date" // 고유 알림장 id

        setContent {
            val commentPrefs = remember { CommentPreferences(this) }
            val comments = remember { mutableStateOf(commentPrefs.loadComments(noticeId)) }
            val newComment = remember { mutableStateOf("") }

            CV2ProjectTheme {
                DetailNoticeScreen(
                    title,
                    content,
                    studentName,
                    date,
                    comments,
                    newComment,
                    noticeId,
                    commentPrefs
                )
            }
        }
    }
}

@Composable
fun DetailNoticeScreen(
    title: String,
    content: String,
    studentName: String,
    date: String,
    comments: MutableState<List<Comment>>,
    newComment: MutableState<String>,
    noticeId: String,
    commentPrefs: CommentPreferences
) {
    val context = LocalContext.current as? Activity
    var showDialog by remember { mutableStateOf(false) }
    var showCommentDialog = remember { mutableStateOf(false) } // 팝업창 상태


    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(color = Color.Black),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Absolute.SpaceBetween
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
                "알림장 내용",
                color = Color.White,
                fontSize = 25.sp
            )

            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete notice",
                modifier = Modifier
                    .padding(end = 15.dp)
                    .size(25.dp)
                    .clickable { showDialog = true },
                tint = Color.White
            )

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("알림장 삭제") },
                    text = { Text("내용이 영구적으로 삭제됩니다. \n삭제하시겠습니까?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                val resultIntent = Intent().apply {
                                    putExtra("delete_title", title)
                                    putExtra("delete_content", content)
                                    putExtra("delete_studentName", studentName)
                                    putExtra("delete_date", date)
                                }
                                // 현재 컨텍스트를 Activity로 캐스팅하여 setResult와 finish 호출
                                context?.apply {
                                    setResult(Activity.RESULT_OK, resultIntent)
                                    finish()
                                }
                            }
                        ) {
                            Text("삭제")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showDialog = false }
                        ) {
                            Text("취소")
                        }
                    }
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // 알림장 세부 내용
            Spacer(modifier = Modifier.size(10.dp))
            // notice 제목
            Text(
                text = title,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(20.dp))
            // 공지사항 내용
            Text(text = content, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(50.dp))
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                // 학생명
                Text(
                    text = "$studentName 학생",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.weight(1f)
                )
                // 작성 날짜
                Text(
                    text = "작성 날짜: $date",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Divider(Modifier.width(1.dp))
        }
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
                .height(300.dp)
        ) {
            // 댓글 표시
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
                            painter = painterResource(R.drawable.delete), // 휴지통 아이콘
                            contentDescription = "댓글 삭제",
                            modifier = Modifier
                                .size(20.dp)
                                .clickable {
                                    val updatedComments = comments.value.toMutableList()
                                    updatedComments.removeAt(index) // 선택한 댓글 삭제
                                    comments.value = updatedComments
                                    commentPrefs.saveComments(
                                        noticeId,
                                        updatedComments
                                    ) // SharedPreferences에 저장
                                }
                        )
                    }
                }
            }
        }
        Button(
            onClick = {
                showCommentDialog.value = true
            },
            modifier = Modifier.padding(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black
            )
        ) {
            Text("댓글 작성", color = Color.White)
        }
    }
    // 댓글 입력 팝업 다이얼로그
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
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newComment.value.isNotEmpty()) {
                            val newCommentData = Comment(
                                author = "사용자", // 실제 사용자 이름으로 변경 가능
                                text = newComment.value,
                                timestamp = SimpleDateFormat(
                                    "MM월 dd일 HH:mm",
                                    Locale.getDefault()
                                ).format(Date())
                            )
                            val updatedComments = comments.value + newCommentData
                            comments.value = updatedComments
                            commentPrefs.saveComments(noticeId, updatedComments)
                            newComment.value = ""
                            showCommentDialog.value = false // 팝업 닫기
                        }
                    }
                ) {
                    Text("등록")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showCommentDialog.value = false }
                ) {
                    Text("취소")
                }
            }
        )
    }
}

