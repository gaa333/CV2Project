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
}
