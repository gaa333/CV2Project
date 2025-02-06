package com.example.cv2project.preferences

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class Comment(val author: String, val text: String, val timestamp: String)

class CommentPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("comment_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveComments(noticeId: String, comments: List<Comment>) {
        val jsonString = gson.toJson(comments)
        prefs.edit().putString("comments_$noticeId", jsonString).apply()
    }

    fun loadComments(noticeId: String): List<Comment> {
        val jsonString = prefs.getString("comments_$noticeId", "[]") ?: "[]"
        return gson.fromJson(jsonString, object : TypeToken<List<Comment>>() {}.type)
    }
}
