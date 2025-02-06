package com.example.cv2project

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun getTodayDate(): String {
    val calendar = Calendar.getInstance()
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val dayOfWeek = SimpleDateFormat("E", Locale.KOREAN).format(calendar.time) // "금" 같은 요일 반환
    return "${day}일 $dayOfWeek"
}