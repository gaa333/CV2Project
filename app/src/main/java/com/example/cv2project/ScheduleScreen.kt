package com.example.cv2project

import android.app.TimePickerDialog
import android.content.Context
import android.widget.CalendarView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * 일정 정보를 담는 데이터 클래스
 */
data class Schedule(val date: String, val time: String, val event: String)

/**
 * 날짜, 시간 포맷 관련 함수들
 */
fun getTodaysDate(): String {
    val sdf = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
    return sdf.format(Date())
}

fun formatDate(year: Int, month: Int, day: Int): String {
    return String.format("%04d년 %02d월 %02d일", year, month, day)
}

fun formatTime(time: String): String {
    // 예: "1230" → "12시 30분"
    return if (time.length == 4) {
        "${time.substring(0, 2)}시 ${time.substring(2, 4)}분"
    } else {
        time
    }
}

/**
 * 스케줄 화면 (Composable)
 * 네비게이션에서 route를 "schedule"로 등록해서 사용
 */
@Composable
fun ScheduleScreen(navController: NavController) {
    val context = LocalContext.current
    var selectedDate by remember { mutableStateOf(getTodaysDate()) }
    val scheduleData = remember { mutableStateListOf<Schedule>() }
    val sharedPreferences = remember {
        context.getSharedPreferences("SchedulePrefs", Context.MODE_PRIVATE)
    }
    val gson = remember { Gson() }

    // 일정 데이터를 불러오는 함수
    fun loadSchedules() {
        val json = sharedPreferences.getString("scheduleData", "[]")
        val type = object : TypeToken<List<Schedule>>() {}.type
        val schedules: List<Schedule> = gson.fromJson(json, type)
        scheduleData.clear()
        scheduleData.addAll(schedules)
    }

    // 일정 데이터를 저장하는 함수
    fun saveSchedules() {
        val json = gson.toJson(scheduleData)
        sharedPreferences.edit().putString("scheduleData", json).apply()
    }

    // Composable이 처음 구성될 때 스케줄 데이터 로드
    LaunchedEffect(Unit) {
        loadSchedules()
    }

    // 선택된 날짜와 동일한 일정을 필터링
    val filteredSchedule by remember {
        derivedStateOf { scheduleData.filter { it.date == selectedDate } }
    }

    // "일정 추가" 다이얼로그 표시 여부
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
            Spacer(modifier = Modifier.weight(0.8f))
            Image(
                painter = painterResource(id = R.drawable.schedule1),
                contentDescription = "알림장",
                modifier = Modifier.size(150.dp)
            )
            Spacer(modifier = Modifier.weight(1.2f))
        }

        // 달력 (CalendarView)
        AndroidView(
            factory = { ctx ->
                CalendarView(ctx).apply {
                    setOnDateChangeListener { _, year, month, dayOfMonth ->
                        selectedDate = formatDate(year, month + 1, dayOfMonth)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // 일정 추가 버튼
        Button(
            onClick = { showDialog = true },
            modifier = Modifier.padding(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text("일정 추가", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 현재 선택된 날짜 표시
        Text(selectedDate, fontSize = 24.sp)

        // 일정 목록
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(filteredSchedule.sortedBy { it.time }) { schedule ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "${formatTime(schedule.time)} ${schedule.event}",
                        fontSize = 18.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = {
                        scheduleData.remove(schedule)
                        saveSchedules() // 일정 삭제 후 저장
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Schedule"
                        )
                    }
                }
            }
        }

        // (선택 사항) "뒤로" 버튼
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("뒤로")
        }
    }

    // "일정 추가" 다이얼로그
    if (showDialog) {
        AddScheduleDialog(
            selectedDate = selectedDate,
            onDismiss = { showDialog = false },
            onAddSchedule = { newSchedule ->
                scheduleData.add(newSchedule)
                saveSchedules() // 일정 추가 후 저장
                showDialog = false
            }
        )
    }
}

/**
 * 일정 추가 다이얼로그
 */
@Composable
fun AddScheduleDialog(
    selectedDate: String,
    onDismiss: () -> Unit,
    onAddSchedule: (Schedule) -> Unit
) {
    var time by remember { mutableStateOf("") }
    var event by remember { mutableStateOf("") }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("새로운 일정 추가") },
        text = {
            Column {
                Button(
                    onClick = {
                        val calendar = Calendar.getInstance()
                        TimePickerDialog(
                            context,
                            { _, hour, minute ->
                                time = String.format("%02d%02d", hour, minute)
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                        ).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4786FF)) // 버튼 색상 변경
                ) {
                    Text(
                        "시간 선택: ${if (time.isEmpty()) "선택 안 됨" else formatTime(time)}",
                        color = Color.White // 글씨 색상 변경
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = event,
                    onValueChange = { event = it },
                    label = { Text("일정 내용") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (time.isNotEmpty() && event.isNotEmpty()) {
                        onAddSchedule(Schedule(selectedDate, time, event))
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4786FF))
            ) {
                Text("추가", color = Color.White)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4786FF))
            ) {
                Text("취소", color = Color.White)
            }
        }
    )
}
