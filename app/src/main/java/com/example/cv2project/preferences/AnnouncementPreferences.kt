package com.example.cv2project.preferences

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AnnouncementPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("announcements_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    // 공지사항 리스트를 SharedPreferences에 저장 (JSON 변환 후 저장)
    fun saveAnnouncements(announcements: List<Triple<String, String, String>>) {
        val json = gson.toJson(announcements)
        prefs.edit().putString("announcement_list", json).apply()
    }

    // SharedPreferences에서 공지사항 리스트 불러오기 (JSON → 리스트로 변환)
    fun loadAnnouncements(): List<Triple<String, String, String>> {
        val json = prefs.getString("announcement_list", null) ?: return emptyList()
        val type = object : TypeToken<List<Triple<String, String, String>>>() {}.type
        return gson.fromJson(json, type)
    }
}