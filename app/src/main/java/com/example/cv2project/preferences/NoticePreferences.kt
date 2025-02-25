package com.example.cv2project.preferences

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class Notice(
    val title: String,
    val content: String,
    val studentName: String,
    val date: String
)

class NoticePreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("notice_prefs", Context.MODE_PRIVATE)

    private val gson = Gson()

    fun saveNotices(notices: List<Notice>) {
        val jsonString = gson.toJson(notices)
        prefs.edit().putString("notice_list", jsonString).apply()
    }

    fun loadNotices(): List<Notice> {
        val jsonString = prefs.getString("notice_list", "[]") ?: "[]"
        return gson.fromJson(jsonString, object : TypeToken<List<Notice>>() {}.type)
    }
    fun loadAndDeleteNotice(noticeId: String): List<Notice> {
        // 1) noticeId를 "제목-날짜" 형태로 파싱
        val parts = noticeId.split("-")
        // 혹은 복잡한 포맷이라면 Regex 또는 다른 로직 사용
        if (parts.size < 2) {
            // noticeId 형식이 잘못되었으면 모든 알림을 반환하거나 적절히 처리
            return loadNotices()
        }

        val noticeTitle = parts[0]
        val noticeDate = parts[1]

        // 2) 기존 알림 목록 불러오기
        val notices = loadNotices().toMutableList()

        // 3) 해당 제목 & 날짜의 Notice를 제외(filterNot)하여 새로운 목록 생성
        val updated = notices.filterNot {
            it.title == noticeTitle && it.date == noticeDate
        }

        // 4) 변경된 목록을 다시 저장
        saveNotices(updated)

        // 5) 삭제 후 목록 반환
        return updated
    }
}
