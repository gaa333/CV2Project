package com.example.cv2project

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.CalendarView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.cv2project.ui.theme.CV2ProjectTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class ScheduleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CV2ProjectTheme {
                ScheduleScreen()
            }
        }
    }
}

@Composable
fun ScheduleScreen() {
    val context = LocalContext.current as? Activity
    var selectedDate by remember { mutableStateOf(getTodayDate()) }
    val scheduleData = remember { mutableStateListOf<Schedule>() }
    val sharedPreferences = context?.getSharedPreferences("SchedulePrefs", Context.MODE_PRIVATE)
    val gson = Gson()

    // 일정 데이터를 불러오는 함수
    fun loadSchedules() {
        val json = sharedPreferences?.getString("scheduleData", "[]")
        val type = object : TypeToken<List<Schedule>>() {}.type
        val schedules: List<Schedule> = gson.fromJson(json, type)
        scheduleData.clear()
        scheduleData.addAll(schedules)
    }

    // 일정 데이터를 저장하는 함수
    fun saveSchedules() {
        val json = gson.toJson(scheduleData)
        sharedPreferences?.edit()?.putString("scheduleData", json)?.apply()
    }

    // 일정 필터링
    val filteredSchedule by remember { derivedStateOf { scheduleData.filter { it.date == selectedDate } } }
    var showDialog by remember { mutableStateOf(false) }

    // 초기 데이터 로드
    LaunchedEffect(Unit) {
        loadSchedules()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("일정표", fontSize = 40.sp, modifier = Modifier.padding(16.dp))

        AndroidView(
            factory = { CalendarView(it).apply {
                setOnDateChangeListener { _, year, month, dayOfMonth ->
                    val formattedDate = formatDate(year, month + 1, dayOfMonth)
                    selectedDate = formattedDate
                }
            } },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { showDialog = true },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("일정 추가")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(selectedDate, fontSize = 24.sp)

        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            items(filteredSchedule.sortedBy { it.time }) { schedule ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("${formatTime(schedule.time)} ${schedule.event}", fontSize = 18.sp, modifier = Modifier.padding(8.dp))
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = {
                        scheduleData.remove(schedule)
                        saveSchedules() // 일정 삭제 후 저장
                    }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Schedule")
                    }
                }
            }
        }

        Button(
            onClick = { context?.finish() },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("뒤로")
        }
    }

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

@Composable
fun AddScheduleDialog(selectedDate: String, onDismiss: () -> Unit, onAddSchedule: (Schedule) -> Unit) {
    var time by remember { mutableStateOf("") }
    var event by remember { mutableStateOf("") }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("새 일정 추가") },
        text = {
            Column {
                Button(onClick = {
                    val calendar = Calendar.getInstance()
                    TimePickerDialog(
                        context,
                        { _, hour, minute -> time = String.format("%02d%02d", hour, minute) },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                    ).show()
                }) {
                    Text("시간 선택: ${if (time.isEmpty()) "선택 안 됨" else formatTime(time)}")
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
                }
            ) {
                Text("추가")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}

// 일정 데이터 클래스
data class Schedule(val date: String, val time: String, val event: String)

// 오늘 날짜를 가져오는 함수
fun getTodayDate(): String {
    val sdf = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
    return sdf.format(Date())
}

// 날짜를 원하는 형식으로 변환하는 함수
fun formatDate(year: Int, month: Int, day: Int): String {
    return String.format("%04d년 %02d월 %02d일", year, month, day)
}

// 시간을 "00시 00분" 형식으로 변환하는 함수
fun formatTime(time: String): String {
    return if (time.length == 4) {
        "${time.substring(0, 2)}시 ${time.substring(2, 4)}분"
    } else {
        time
    }
}
