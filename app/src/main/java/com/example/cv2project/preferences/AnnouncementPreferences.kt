package com.example.cv2project.preferences

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class Announcement(
    val title: String = "",
    val content: String = "",
    val date: String = ""
)
class AnnouncementPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("announcements_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveAnnouncements(announcements: List<Announcement>) {
        val jsonString = gson.toJson(announcements)
        prefs.edit().putString("announcement_list", jsonString).apply()
    }

    fun loadAnnouncements(): List<Announcement> {
        val jsonString = prefs.getString("announcement_list", "[]") ?: "[]"
        return gson.fromJson(jsonString, object : TypeToken<List<Announcement>>() {}.type)
    }
}