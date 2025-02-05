package com.example.cv2project.preferences

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class Student(
    val name: String,
    val age: Int
)

class StudentPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("student_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveStudents(students: List<Student>) {
        val jsonString = gson.toJson(students)
        prefs.edit().putString("student_list", jsonString).apply()
    }

    fun loadStudents(): List<Student> {
        val jsonString = prefs.getString("student_list", "[]") ?: "[]"
        return gson.fromJson(jsonString, object : TypeToken<List<Student>>() {}.type)
    }
}